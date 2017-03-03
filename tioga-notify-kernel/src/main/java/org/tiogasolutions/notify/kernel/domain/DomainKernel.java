package org.tiogasolutions.notify.kernel.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.id.IdGenerator;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.kernel.task.TaskGenerator;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainStatus;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DomainKernel {

    private final static String DEFAULT_PASSWORD = "GoFish";
    private final DomainStore domainStore;
    private final IdGenerator domainKeyGenerator;
    private final Map<String, DomainProfile> domainProfileMap = new HashMap<>();
    private final DomainProfile systemDomain;
    private final TaskGenerator taskGenerator;
    private final EventBus eventBus;

    @Autowired
    public DomainKernel(DomainStore domainStore,
                        @Qualifier("DomainKeyGenerator") IdGenerator idGenerator,
                        TaskGenerator taskGenerator,
                        EventBus eventBus) {

        this.domainStore = domainStore;
        this.domainKeyGenerator = idGenerator;
        this.taskGenerator = taskGenerator;
        this.eventBus = eventBus;
        RouteCatalog routeCatalog = new RouteCatalog(null, null);
        systemDomain = new DomainProfile("0000000000", null, "system", DomainStatus.ACTIVE, null, null, null, null, routeCatalog);

        // Initialize the domain profile map
        for (DomainProfileEntity domainProfileEntity : domainStore.queryAll()) {
            DomainProfile domainProfile = domainProfileEntity.toModel();
            domainProfileMap.put(domainProfile.getApiKey(), domainProfile);
        }
    }

    public boolean hasDomain(String domainName) {
        for (DomainProfile profile : domainProfileMap.values()) {
            if (profile.getDomainName().equalsIgnoreCase(domainName)) {
                return true;
            }
        }
        return false;
    }

    public DomainProfile getOrCreateDomain(String domainName) {
        if (hasDomain(domainName)) {
            return findByDomainName(domainName);
        } else {
            return createDomain(domainName);
        }
    }

    public DomainProfile findByApiKey(String apiKey) {
        DomainProfile profile = domainProfileMap.get(apiKey);
        if (profile == null) {
            throw ApiNotFoundException.notFound("Domain not found with api key: " + apiKey);
        }
        return profile;
    }

    public DomainProfile findByDomainName(String domainName) {
        for (DomainProfile profile : domainProfileMap.values()) {
            if (profile.getDomainName().equalsIgnoreCase(domainName)) {
                return profile;
            }
        }
        throw ApiNotFoundException.notFound("The specified domain name was not found.");
    }

    public List<DomainProfile> listActiveDomainProfiles() {
        return domainProfileMap.values()
                .stream()
                .filter(d -> d.getDomainStatus() == DomainStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<NotificationDomain> listActiveNotificationDomains() {
        return domainProfileMap.values()
                .stream()
                .filter(d -> d.getDomainStatus() == DomainStatus.ACTIVE)
                .map(this::notificationDomain)
                .collect(Collectors.toList());
    }

    public DomainProfile getSystemDomain() {
        return systemDomain;
    }

    public DomainProfile createDomain(String domainName) {
        DomainProfile domainProfile = domainStore.createDomain(domainName, domainKeyGenerator.newId(), DEFAULT_PASSWORD).toModel();
        domainProfileMap.put(domainProfile.getApiKey(), domainProfile);
        return domainProfile;
    }

    public void recreateDomain(String domainName, String apiKey, String password) {
        DomainProfile domainProfile = domainStore.recreateDomain(domainName, apiKey, password);
        domainProfileMap.put(domainProfile.getApiKey(), domainProfile);
    }

    public void deleteDomain(String domainName) {
        // throws ApiNotFound if not found.
        DomainProfileEntity domainProfile = domainStore.findByDomainName(domainName);

        domainStore.deleteDomain(domainProfile);
        domainProfileMap.remove(domainProfile.getApiKey());
    }

    /**
     * Update the RouteCatalog for the given domain
     *
     * @param domainProfile -
     * @param routeCatalog  - the new RouteCatalog
     * @return DomainProfile the updated domain profile
     */
    public DomainProfile updateRouteCatalog(DomainProfile domainProfile, RouteCatalog routeCatalog) {
        DomainProfileEntity domainProfileEntity = domainStore.findByDomainName(domainProfile.getDomainName());

        domainProfileEntity.setRouteCatalog(routeCatalog);
        domainProfileEntity = domainStore.save(domainProfileEntity);
        domainProfile = domainProfileEntity.toModel();
        domainProfileMap.put(domainProfile.getApiKey(), domainProfileEntity.toModel());
        return domainProfile;
    }

    public DomainSummary fetchSummary(String domainName) {
        return domainStore.fetchSummary(domainName);
    }

    public NotificationDomain notificationDomain(ExecutionContext ec) {
        DomainProfile domainProfile = findByApiKey(ec.getApiKey());
        CouchDatabase notificationDatabase = domainStore.notificationDb(domainProfile);
        return new NotificationDomain(
                ec.getDomainName(),
                notificationDatabase,
                domainProfile.getRouteCatalog(),
                taskGenerator,
                eventBus);
    }

    public NotificationDomain notificationDomain(String domainName) {
        DomainProfile domainProfile = findByDomainName(domainName);
        CouchDatabase notificationDatabase = domainStore.notificationDb(domainProfile);
        return new NotificationDomain(
                domainName,
                notificationDatabase,
                domainProfile.getRouteCatalog(),
                taskGenerator,
                eventBus);
    }

    public NotificationDomain notificationDomain(DomainProfile domainProfile) {
        CouchDatabase notificationDatabase = domainStore.notificationDb(domainProfile);
        return new NotificationDomain(
                domainProfile.getDomainName(),
                notificationDatabase,
                domainProfile.getRouteCatalog(),
                taskGenerator,
                eventBus);
    }

    public CouchDatabase requestDb(DomainProfile domainProfile) {
        return domainStore.requestDb(domainProfile);
    }
}

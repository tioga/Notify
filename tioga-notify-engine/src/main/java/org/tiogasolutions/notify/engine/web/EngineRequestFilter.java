package org.tiogasolutions.notify.engine.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.notify.kernel.admin.AdminKernel;
import org.tiogasolutions.notify.kernel.config.SystemConfiguration;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.tiogasolutions.notify.kernel.Paths.*;

/**
 * This is the "global" filter for the Engine. It's primary responsibility is
 * for managing the execution context over the entire lifecycle of a request.
 * <p>
 * All other filters should be processed after this one.
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class EngineRequestFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(EngineRequestFilter.class);
    @Context
    Application application;
    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders headers;

    @Autowired // Injected by CDI, not Spring
    private ExecutionManager executionManager;

    @Autowired // Injected by CDI, not Spring
    private DomainKernel domainKernel;

    @Autowired // Injected by CDI, not Spring
    private AdminKernel adminKernel;

    @Autowired
    private SystemConfiguration systemConfiguration;

    public EngineRequestFilter() {
        log.info("Created");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String baseUri = uriInfo.getBaseUri().toString();
        String requestUri = uriInfo.getRequestUri().toString();

        String path = requestUri.substring(baseUri.length() - 1);

        List<String> anonymous = Arrays.asList(
                $root,
                $api, $api + "/",
                $static, $static + "/",
                // $api_v1, $api_v1+"/",
                $api_v2_status,
                $health_check,
                $favicon
        );

        try {
            if (path.startsWith("/static/") || anonymous.contains(path)) {
                // noinspection UnnecessaryReturnStatement
                return;

            } else if (path.equals("/api/v2/admin") || path.startsWith("/api/v2/admin/")) {
                authenticateAdminRequest(requestContext);

            } else if (path.startsWith($app)) {
                authenticateAdminRequest(requestContext);

            } else {
                authenticateClientRequest(requestContext);
            }
        } catch (NotAuthorizedException e) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Notify\"")
                    .type("text/plain")
                    .entity("Not authorized")
                    .build());
        }
    }

    private void authenticateClientRequest(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return; // do not authenticate OPTIONS calls.
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || authHeader.startsWith("Basic ") == false) {
            throw new NotAuthorizedException("Notify");
        } else {
            authHeader = authHeader.substring(6);
        }

        byte[] bytes = DatatypeConverter.parseBase64Binary(authHeader);
        String basicAuth = new String(bytes, StandardCharsets.UTF_8);

        int pos = basicAuth.indexOf(":");

        String apiKey;
        String apiPassword;

        if (pos < 0) {
            throw new NotAuthorizedException("API");
        }

        apiKey = basicAuth.substring(0, pos);
        apiPassword = basicAuth.substring(pos + 1);

        if (StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(apiPassword)) {
            throw new NotAuthorizedException("API");
        }

        DomainProfile domainProfile;

        if (apiKey.equals("system") && apiPassword.equals("changeme")) {
            // HACK/TODO - remove hard coded password.
            domainProfile = domainKernel.getSystemDomain();

        } else {

            try {
                domainProfile = domainKernel.findByApiKey(apiKey);
            } catch (ApiNotFoundException e) {
                throw new NotAuthorizedException("API");
            }

            if (EqualsUtils.objectsNotEqual(apiPassword, domainProfile.getApiPassword())) {
                throw new NotAuthorizedException("API");
                // Specific message allows for probing of accounts.
                // throw new NotAuthorizedException("Invalid password");
            }
        }

        if (EqualsUtils.objectsNotEqual(apiPassword, domainProfile.getApiPassword())) {
            throw new NotAuthorizedException("API");
            // Specific message allows for probing of accounts.
            // throw new NotAuthorizedException("Invalid password");
        }

        final SecurityContext securityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new ClientSecurityContext(securityContext, domainProfile));

        executionManager.newApiContext(domainProfile, uriInfo, headers);
    }

    private void authenticateAdminRequest(ContainerRequestContext requestContext) {
        String authenticatedusername = null;
        if (systemConfiguration.isAutoAuthAdmin()) {
            authenticatedusername = "auto-admin";
        } else {
            String authHeader = requestContext.getHeaderString("Authorization");

            if (authHeader == null) {
                throw new NotAuthorizedException("API");
            } else if (authHeader.startsWith("Basic ") == false) {
                throw new NotAuthorizedException("API");
            } else {
                authHeader = authHeader.substring(6);
            }

            byte[] bytes = DatatypeConverter.parseBase64Binary(authHeader);
            String basicAuth = new String(bytes, StandardCharsets.UTF_8);

            int pos = basicAuth.indexOf(":");

            String username;
            String password;

            if (pos < 0) {
                username = basicAuth;
                password = null;

            } else {
                username = basicAuth.substring(0, pos);
                password = basicAuth.substring(pos + 1);
            }

            // throws NotAuthorizedException if not a valid username and password
            adminKernel.authorize(username, password);
            authenticatedusername = username;
        }


        final SecurityContext securityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new AdminSecurityContext(securityContext, authenticatedusername));
    }

    private class AdminSecurityContext implements SecurityContext {
        private final boolean secure;
        private final String username;

        public AdminSecurityContext(SecurityContext securityContext, String username) {
            this.username = username;
            this.secure = securityContext.isSecure();
        }

        public String getUsername() {
            return username;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "BASIC_AUTH";
        }

        @Override
        public Principal getUserPrincipal() {
            return this::getUsername;
        }
    }

    private class ClientSecurityContext implements SecurityContext {
        private final boolean secure;
        private final String domainName;
        private final Principal principal;

        public ClientSecurityContext(SecurityContext securityContext, DomainProfile domain) {
            this.secure = securityContext.isSecure();
            this.domainName = domain.getDomainName();
            this.principal = this::getDomainName;
        }

        public String getDomainName() {
            return domainName;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "BASIC_AUTH";
        }

        @Override
        public Principal getUserPrincipal() {
            return principal;
        }
    }
}

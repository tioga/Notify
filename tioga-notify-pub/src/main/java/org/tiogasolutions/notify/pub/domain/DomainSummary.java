package org.tiogasolutions.notify.pub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.common.TopicInfo;
import org.tiogasolutions.notify.pub.common.TraitInfo;

import java.util.List;
import java.util.Optional;

/**
 * Created by harlan on 5/22/15.
 */
public class DomainSummary {
    private final List<TopicInfo> topics;
    private final List<TraitInfo> traits;

    @JsonCreator
    public DomainSummary(@JsonProperty("topics") List<TopicInfo> topics,
                         @JsonProperty("traits") List<TraitInfo> traits) {
        this.topics = topics;
        this.traits = traits;
    }

    public List<TopicInfo> getTopics() {
        return topics;
    }

    public List<TraitInfo> getTraits() {
        return traits;
    }

    public Optional<TopicInfo> findTopicInfo(String topicName) {

        TopicInfo found = null;
        for (TopicInfo info : topics) {
            if (info.getName().equalsIgnoreCase(topicName)) {
                found = info;
            }
        }

        return topics.stream()
                .filter(t -> t.getName().equalsIgnoreCase(topicName))
                .findFirst();
    }

    public Optional<TraitInfo> findTraitInfo(String traitKey) {
        return traits.stream()
                .filter(t -> t.getKey().equalsIgnoreCase(traitKey))
                .findFirst();
    }
}

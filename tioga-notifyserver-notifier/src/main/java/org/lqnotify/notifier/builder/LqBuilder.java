package org.lqnotify.notifier.builder;

import org.lqnotify.notifier.request.*;
import org.lqnotify.notifier.sender.LqSender;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 10:44 PM
 */
public class LqBuilder {
  private final LqSender sender;
  private final LqBuilderCallbacks callbacks;
  private String topic;
  private String trackingId;
  private String summary;
  private ZonedDateTime createdAt;
  private LqExceptionInfo exceptionInfo;
  private List<LqTrait> traits = new ArrayList<>();
  private List<LqAttachment> attachments = new ArrayList<>();

  public LqBuilder(LqSender sender, LqBuilderCallbacks callbacks) {
    this.sender = sender;
    this.callbacks = callbacks.copy();
    callbacks.callBegin(this);
  }

  public LqBuilder onBegin(LqBuilderCallback callback) {
    callbacks.onBegin(callback);
    return this;
  }

  public LqBuilder onBeforeSend(LqBuilderCallback callback) {
    callbacks.onBeforeSend(callback);
    return this;
  }

  public Future<LqResponse> send() {
    callbacks.callBeforeSend(this);
    LqRequest request = new LqRequest(
      topic,
      summary,
      trackingId,
      createdAt,
      LqTrait.toTraitMap(traits),
      exceptionInfo,
      attachments);

    return sender.send(request);
  }

  public LqBuilder topic(String topic) {
    this.topic = topic;
    return this;
  }

  public LqBuilder trackingId(String trackingId) {
    this.trackingId = trackingId;
    return this;
  }

  public LqBuilder summary(String summary) {
    this.summary = summary;
    return this;
  }

  public LqBuilder summary(String format, Object... args) {
    this.summary = String.format(format, args);
    return this;
  }

  public LqBuilder createdAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LqBuilder trait(String name, Object value) {
    String str = (value != null) ? value.toString() : null;
    traits.add(new LqTrait(name, str));
    return this;
  }

  public LqBuilder traits(String... traits) {
    this.traits.addAll(LqTrait.toTraits(Arrays.asList(traits)));
    return this;
  }

  public LqBuilder traits(Map<String, String> traitMap) {
    this.traits.addAll(LqTrait.toTraits(traitMap));
    return this;
  }

  public LqBuilder exception(Throwable exception) {
    this.exceptionInfo = new LqExceptionInfo(exception);
    return this;
  }

  public LqBuilder attach(String name, String contentType, byte[] content) {
    this.attachments.add(new LqAttachment(name, contentType, content));
    return this;
  }

  public LqBuilder attach(String name, String contentType, InputStream inputStream) {
    this.attachments.add(new LqAttachment(name, contentType, inputStream));
    return this;
  }

  public LqBuilder attach(String name, String contentType, String content) {
    this.attachments.add(new LqAttachment(name, contentType, content));
    return this;
  }

  public String getTopic() {
    return topic;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getSummary() {
    return summary;
  }

  public LqExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public List<LqTrait> getTraits() {
    return traits;
  }

  public List<LqAttachment> getAttachments() {
    return attachments;
  }
}

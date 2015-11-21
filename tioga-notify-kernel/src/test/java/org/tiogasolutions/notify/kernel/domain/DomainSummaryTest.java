package org.tiogasolutions.notify.kernel.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.common.TopicInfo;
import org.tiogasolutions.notify.pub.common.TraitInfo;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class DomainSummaryTest extends AbstractSpringTest {
  private static String SUMMARY_TEST_TOPIC_1 = "SUMMARY_TEST_TOPIC_1";
  private static String SUMMARY_TEST_TOPIC_2 = "SUMMARY_TEST_TOPIC_2";
  private static String SUMMARY_TEST_TRAIT_1 = "SUMMARY_TEST_TRAIT_1";
  private static String SUMMARY_TEST_TRAIT_2 = "SUMMARY_TEST_TRAIT_2";

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private NotificationKernel notificationKernel;

  @Autowired
  private DomainKernel domainKernel;

  private ExecutionContext executionContext;

  @BeforeClass
  public void beforeClass() {
    executionContext = executionManager.newApiContext(TestFactory.API_KEY);

    try {
      ZonedDateTime tenYearsAgo = ZonedDateTime.now().minusYears(10);
      // Create a few test notifications.
      CreateNotification create = new CreateNotification(
          SUMMARY_TEST_TOPIC_1,
          "some message",
          "store-test-9000",
          tenYearsAgo,
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap(SUMMARY_TEST_TRAIT_1));
      notificationKernel.createNotification(create);
      create = new CreateNotification(
          SUMMARY_TEST_TOPIC_1,
          "some message",
          "store-test-9001",
          ZonedDateTime.now(),
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap(SUMMARY_TEST_TRAIT_1 + ":green"));
      notificationKernel.createNotification(create);
      create = new CreateNotification(
          SUMMARY_TEST_TOPIC_2,
          "some message",
          "store-test-9002",
          ZonedDateTime.now(),
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap(SUMMARY_TEST_TRAIT_2));
      notificationKernel.createNotification(create);

    } finally {
      executionManager.clearContext();
    }

  }

  @BeforeMethod
  public void beforeMethod() {
    executionManager.newApiContext(TestFactory.API_KEY);
  }

  @AfterMethod
  public void afterMethod() {
    executionManager.clearContext();
  }

  public void fetchDomainSummary() {
    DomainSummary domainSummary = domainKernel.fetchSummary(executionContext.getDomainName());
    assertTrue(domainSummary.getTopics().size() >= 2);
    assertTrue(domainSummary.getTraits().size() >= 2);

    Optional<TopicInfo> topicInfo = domainSummary.findTopicInfo(SUMMARY_TEST_TOPIC_1);
    assertTrue(topicInfo.isPresent());
    assertEquals(topicInfo.get().getCount(), 2);

    topicInfo = domainSummary.findTopicInfo(SUMMARY_TEST_TOPIC_2);
    assertTrue(topicInfo.isPresent());
    assertEquals(topicInfo.get().getCount(), 1);

    Optional<TraitInfo> traitInfo = domainSummary.findTraitInfo(SUMMARY_TEST_TRAIT_1);
    assertTrue(traitInfo.isPresent());
    assertEquals(traitInfo.get().getCount(), 2);

    traitInfo = domainSummary.findTraitInfo(SUMMARY_TEST_TRAIT_2);
    assertTrue(traitInfo.isPresent());
    assertEquals(traitInfo.get().getCount(), 1);
  }


}
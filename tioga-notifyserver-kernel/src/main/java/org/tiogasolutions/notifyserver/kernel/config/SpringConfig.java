/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

package org.tiogasolutions.notifyserver.kernel.config;

import org.tiogasolutions.dev.common.id.IdGenerator;
import org.tiogasolutions.dev.common.id.TwoPartIdGenerator;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.domain.validation.BeanValidator;
import org.tiogasolutions.dev.domain.validation.Jsr349BeanValidator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.validation.Validation;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * User: harlan
 * Date: 10/19/13
 * Time: 7:05 PM
 */
@Configuration
@EnableAspectJAutoProxy
public class SpringConfig {

  @Bean
  public JsonTranslator jsonTranslator() {
    return new TiogaJacksonTranslator();
  }

  @Bean
  public IdGenerator idGenerator() {
    return new TwoPartIdGenerator();
  }

  @Bean
  public BeanValidator validator() {
    return new Jsr349BeanValidator(Validation.buildDefaultValidatorFactory());
  }

  @Bean(name = "DomainKeyGenerator")
  //@Qualifier("DomainKeyGenerator")
  public IdGenerator domainKeyGenerator() {
    return new TwoPartIdGenerator("%s-%s", ZonedDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), "ABCDFGHJKLMNPQRSTVWXYZ", 20, 5, 7);
  }

}

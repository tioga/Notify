/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.notify.test;

import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.DateUtils;

import java.time.ZoneId;

@Component
public class TestFixture {

  public static final ZoneId westCoastTimeZone = DateUtils.PDT;

  public TestFixture() {
  }
}

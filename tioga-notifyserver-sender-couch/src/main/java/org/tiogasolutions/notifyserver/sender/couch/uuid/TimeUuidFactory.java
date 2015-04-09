/*
 * UUIDGen.java
 *
 * Created on 09.08.2003.
 *
 * eaio: UUID - an implementation of the UUID specification
 * Copyright (c) 2003-2013 Johann Burkard (jb@eaio.com) http://eaio.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.tiogasolutions.notifyserver.sender.couch.uuid;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class contains methods to generate UUID fields. These methods have been
 * refactored out of {@link TimeUuid}.
 * <p>
 * Starting with version 2, this implementation tries to obtain the MAC address
 * of the network card. Under Microsoft Windows, the <code>ifconfig</code>
 * command is used which may pop up a command window in Java Virtual Machines
 * prior to 1.4 once this class is initialized. The command window is closed
 * automatically.
 * <p>
 * The MAC address code has been tested extensively in Microsoft Windows,
 * Linux, Solaris 8, HP-UX 11, but should work in MacOS X and BSDs, too.
 * <p>
 * If you use JDK 6 or later, the code in {@link InterfaceAddress} will be used.
 *
 * @author <a href="mailto:jb@eaio.de">Johann Burkard</a>
 * @version $Id: UUIDGen.java 4714 2012-03-16 11:43:28Z johann $
 * @see <a href="http://johannburkard.de/software/uuid/">UUID</a>
 * @see TimeUuid
 */
/*protected*/ final class TimeUuidFactory {

  /**
   * The last time value. Used to remove duplicate UUIDs.
   */
  private static AtomicLong lastTime = new AtomicLong(Long.MIN_VALUE);

  /**
   * The cached MAC address.
   */
  private static String macAddress = null;

  /**
   * The current clock and node value.
   */
  private static long clockSeqAndNode = 0x8000000000000000L;

  static {
    macAddress = getHardwareAddress();
    clockSeqAndNode |= TimeUuidHexUtil.parseLong(macAddress);
    clockSeqAndNode |= (long) (Math.random() * 0x3FFF) << 48;
  }

  private static void close(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      try {
        closeable.close();
      } catch (IOException ignored) {/* ignored */}
    }
  }

  /**
   * Returns the current clockSeqAndNode value.
   *
   * @return the clockSeqAndNode value
   * @see TimeUuid#getClockSeqAndNode()
   */
  public static long getClockSeqAndNode() {
    return clockSeqAndNode;
  }

  /**
   * Generates a new time field. Each time field is unique and larger than the
   * previously generated time field.
   *
   * @return a new time value
   * @see TimeUuid#getTime()
   */
  public static long newTime() {
    return createTime(System.currentTimeMillis());
  }

  /**
   * Creates a new time field from the given timestamp. Note that even identical
   * values of <code>currentTimeMillis</code> will produce different time fields.
   *
   * @param currentTimeMillis the timestamp
   * @return a new time value
   * @see TimeUuid#getTime()
   */
  public static long createTime(long currentTimeMillis) {

    long time;

    // UTC time

    long timeMillis = (currentTimeMillis * 10000) + 0x01B21DD213814000L;

    while (true) {
      long current = lastTime.get();
      if (timeMillis > current) {
        if (lastTime.compareAndSet(current, timeMillis)) {
          break;
        }
      } else {
        if (lastTime.compareAndSet(current, current + 1)) {
          timeMillis = current + 1;
          break;
        }
      }
    }

    // time low

    time = timeMillis << 32;

    // time mid

    time |= (timeMillis & 0xFFFF00000000L) >> 16;

    // time hi and version

    time |= 0x1000 | ((timeMillis >> 48) & 0x0FFF); // version 1

    return time;

  }

  /**
   * Returns the MAC address. Not guaranteed to return anything.
   *
   * @return the MAC address, may be <code>null</code>
   */
  public static String getMACAddress() {
    return macAddress;
  }

  /**
   * Scans MAC addresses for good ones.
   */
  public static String getHardwareAddress() {
    String out = null;
    try {
      Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
      if (ifs != null) {
        while (ifs.hasMoreElements()) {
          NetworkInterface iface = ifs.nextElement();
          byte[] hardware = iface.getHardwareAddress();
          if (hardware != null && hardware.length == 6
              && hardware[1] != (byte) 0xff) {
            out = TimeUuidHexUtil.append(new StringBuilder(36), hardware).toString();
            break;
          }
        }
      }
    } catch (SocketException ignore) {/* ignore */}

    return out;
  }
}
/*
 * Copyright 2012 Harlan Noonkester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.notify.sender.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;

/**
 * User: harlan
 * Date: 10/11/13
 * Time: 12:11 AM
 */
public class SslConfig {

  private static final Logger log = LoggerFactory.getLogger(SslConfig.class);
  private final SSLContext sslContext;
  private final SSLSocketFactory sslSocketFactory;
  private final KeyStore keyStore;

  public SslConfig(String keyStoreURI, String keyStorePass) throws SslException {

    try {
      // Replace back slashes with forward slashes, helpful when a root path comes from windows.
      keyStoreURI = keyStoreURI.replaceAll("(\\\\+)+", "/");
      this.keyStore = initKeyStore(new URI(keyStoreURI), keyStorePass);
    } catch (URISyntaxException e) {
      throw new SslException("Exception creating URI for " + keyStoreURI, e);
    }

    try {
      // Initialize trust store manager.
      TrustManagerFactory tmf = newTrustManagerFactory();

      // Create and initialize SSLContext, use a keyManager if we were given a key pass (for client auth).
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(new KeyManager[0], tmf.getTrustManagers(), null);

      // Keep reference to sslSocketFactory so it can be utilized by our VistaHttpInvokerExecutor.
      sslSocketFactory = sslContext.getSocketFactory();
    } catch (Exception ex) {
      log.error("Error initializing SSl Socket Factory.", ex);
      throw new SslException("Exception initializing SSLContext.", ex);
    }
  }

  public SSLContext getSSLContext() {
    return sslContext;
  }

  public SSLSocketFactory getSSLSocketFactory() {
    return sslSocketFactory;
  }

  protected KeyStore initKeyStore(URI keyStoreURI, String keyStorePass) throws SslException {
    InputStream inputStream = null;
    try {

      // Open our input stream to read the keystore from.
      KeyStore localKeyStore = KeyStore.getInstance("JKS");
      if (keyStoreURI.getScheme() == null) {
        throw new SslException("KeyStoreURI schema is null, please specify schema (file:, classpath:, etc.).");
      } else if (keyStoreURI.getScheme().equalsIgnoreCase("classpath")) {
        URL url = getClass().getResource(keyStoreURI.getPath());
        if (url == null) {
          throw new SslException("Could not find keystore file at " + keyStoreURI);
        }
        inputStream = url.openStream();
      } else {
        inputStream = keyStoreURI.toURL().openStream();
      }

      // Load the keystore.
      localKeyStore.load(inputStream, keyStorePass.toCharArray());

      return localKeyStore;

    } catch (Exception ex) {
      log.error("Exception loading keystore.", ex);
      throw new SslException("Exception loading keystore.", ex);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          log.error("Error closing input stream in SslSetup", e);
        }
      }
    }
  }

  protected TrustManagerFactory newTrustManagerFactory() throws SslException {
    // Initialize KeyManagerFactory
    try {
      // Initialize TrustManagerFactory.
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(keyStore);

      return tmf;

    } catch (Exception ex) {
      throw new SslException("Exception initializing SSLContext in PaymentSubSystem.", ex);
    }
  }

}


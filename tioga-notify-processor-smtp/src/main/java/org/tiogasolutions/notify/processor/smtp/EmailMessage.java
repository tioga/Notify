/*
 * Copyright 2012 Jacob D Parr
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

package org.tiogasolutions.notify.processor.smtp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.domain.comm.AuthenticationMethod;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.util.*;

public class EmailMessage {

    private static Log log = LogFactory.getLog(EmailMessage.class);
    protected final List<InternetAddress> toAddresses = new ArrayList<InternetAddress>();
    protected final List<InternetAddress> replyToAddress = new ArrayList<InternetAddress>();
    protected final List<File> attachments = new ArrayList<File>();
    protected String host;
    protected String port;
    protected InternetAddress fromAddress;
    protected String subject;
    protected String text;
    protected String html;
    private AuthenticationMethod authenticationMethod = AuthenticationMethod.NONE;
    private String userName;
    private String password;

    public EmailMessage(String host, String port, String address) throws EmailMessageException {
        this(host, port, Arrays.asList(address));
    }

    public EmailMessage(String host, String port, List<String> addresses) throws EmailMessageException {

        this.host = host;
        this.port = port;

        for (String address : addresses) {
            try {
                InternetAddress[] list = InternetAddress.parse(address);
                toAddresses.addAll(Arrays.asList(list));
            } catch (Exception ex) {
                throw new EmailMessageException("Exception parsing the email address: " + address);
            }
        }
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setAuthentication(AuthenticationMethod authenticationMethod, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.authenticationMethod = authenticationMethod;
    }

    public void send() throws EmailMessageException {
        try {
            if (toAddresses.size() == 0) {
                throw new EmailMessageException("At least one recipient must be specified in order to send the message.");
            }

            // We have to put the SMPT server (host) into a set of properties.
            Properties props = new Properties();
            if (host != null) props.put("mail.smtp.host", host);
            if (port != null) props.put("mail.smtp.port", port);

            Authenticator authenticator;

            if (AuthenticationMethod.NONE == authenticationMethod) {
                authenticator = null;

            } else if (AuthenticationMethod.SSL == authenticationMethod) {
                authenticator = newAuthenticator();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", SSLSocketFactory.class.getName());

            } else if (AuthenticationMethod.TLS == authenticationMethod) {
                authenticator = newAuthenticator();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true");

            } else {
                String msg = String.format("The authentication method %s is not supported.", authenticationMethod);
                throw new IllegalArgumentException(msg);
            }

            Session session = Session.getInstance(props, authenticator);
            send(session);

        } catch (Exception ex) {
            throw new EmailMessageException("Exception sending email\n" + toString(), ex);
        }
    }

    public Authenticator newAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
    }

    /**
     * @param session the applications current session.
     * @throws EmailMessageException in response to any other type of exception.
     */
    @SuppressWarnings({"ConstantConditions"})
    protected void send(Session session) throws EmailMessageException {
        try {

            // create a message
            MimeMessage msg = new MimeMessage(session);

            //set some of the basic attributes.
            msg.setFrom(fromAddress);
            msg.setRecipients(Message.RecipientType.TO, ReflectUtils.toArray(InternetAddress.class, toAddresses));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            if (replyToAddress.isEmpty() == false) {
                msg.setReplyTo(ReflectUtils.toArray(InternetAddress.class, replyToAddress));
            }

            // create the Multipart and add set it as the content of the message
            Multipart multipart = new MimeMultipart();
            msg.setContent(multipart);

            // create and fill the HTML part of the messgae if it exists
            if (html != null) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(html, "UTF-8", "html");
                multipart.addBodyPart(bodyPart);
            }

            // create and fill the text part of the messgae if it exists
            if (text != null) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(text, "UTF-8", "plain");
                multipart.addBodyPart(bodyPart);
            }

            if (html == null && text == null) {
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText("", "UTF-8", "plain");
                multipart.addBodyPart(bodyPart);
            }

            // remove any nulls from the list of attachments.
            while (attachments.remove(null)) { /* keep going */ }

            // Attach any files that we have, making sure that they exist first
            for (File file : attachments) {
                if (file.exists() == false) {
                    throw new EmailMessageException("The file \"" + file.getAbsolutePath() + "\" does not exist.");
                } else {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            // send the message
            Transport.send(msg);

        } catch (EmailMessageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new EmailMessageException("Exception sending email\n" + toString(), ex);
        }
    }

    public void send(String subject, String text, String html, File... attachments) throws EmailMessageException {
        send(subject, text, html, Arrays.asList(attachments));
    }

    public void send(String subject, String text, String html, List<File> attachments) throws EmailMessageException {
        setSubject(subject);
        setText(text);
        setHtml(html);
        addAttachments(attachments);
        send();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public List<String> getTo() {
        List<String> retVal = new ArrayList<String>();
        for (InternetAddress addr : toAddresses) {
            retVal.add(addr.getAddress());
        }
        return retVal;
    }

    public String getFrom() {
        return (fromAddress == null) ? null : fromAddress.getAddress();
    }

    public void setFrom(String from) throws EmailMessageException {
        if (from == null) {
            from = "DO_NOT_REPLY";
        }
        try {
            InternetAddress[] addresses = InternetAddress.parse(from);
            if (addresses.length != 1) {
                throw new EmailMessageException("One and only one \"from\" address may be specified, " + addresses.length + " were found: " + from);
            }
            fromAddress = addresses[0];

        } catch (EmailMessageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new EmailMessageException("Exception parsing the email addresses");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getReplyTo() {
        List<String> retVal = new ArrayList<String>();
        for (InternetAddress addr : replyToAddress) {
            retVal.add(addr.getAddress());
        }
        return retVal;
    }

    public void addReplyTo(String... addresses) throws EmailMessageException {
        addReplyTo(Arrays.asList(addresses));
    }

    public void addReplyTo(List<String> replyAddress) throws EmailMessageException {
        for (String address : replyAddress) {
            try {
                InternetAddress[] list = InternetAddress.parse(address);
                replyToAddress.addAll(Arrays.asList(list));
            } catch (Exception ex) {
                throw new EmailMessageException("Exception parsing the email address: " + address);
            }
        }
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String value) {
        html = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<File> getAttachments() {
        return new ArrayList<File>(attachments);
    }

    public void addAttachments(List<File> values) {
        attachments.addAll(values);
    }

    public void addAttachments(File... values) {
        attachments.addAll(Arrays.asList(values));
    }

    @Override
    public String toString() {
        String retVal = "to: ";
        List<String> tos = getTo();
        for (String to : tos) {
            retVal += to;
            if (tos.iterator().hasNext()) {
                retVal += ", ";
            }
        }
        retVal += " subject: " + getSubject();
        return retVal;
    }

}
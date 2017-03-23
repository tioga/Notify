package org.tiogasolutions.notify.kernel.message;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.tiogasolutions.lib.thymeleaf.ThymeleafFactory;

import java.util.HashMap;
import java.util.Map;

public class ThymeleafMessageBuilder {
  private static Logger log = LoggerFactory.getLogger(ThymeleafMessageBuilder.class);

  private ThymeleafFactory thymeleafFactory;

  public ThymeleafMessageBuilder() {
    this.thymeleafFactory = new ThymeleafFactory();
  }

  public ThymeleafMessageBuilder(ThymeleafFactory thymeleafFactory) {
    this.thymeleafFactory = thymeleafFactory;
  }

  public String createMessage(DomainProfile domainProfile, Notification notification, Task task, String templatePath) {
    Context context = new Context();
    context.setVariable("it", new MessageModel(domainProfile, notification, task));
    String text = thymeleafFactory.process(templatePath, context);

    if (log.isTraceEnabled()) {
      log.trace("Text after Thymeleaf processing: " + text);
    }

    // Just a couple more issues. Thymeleaf is technically processing HTML, because of that,
    // some values may need to be HTML encode which we will want to decode here.
    text = decodeHtml(text);

    if (log.isTraceEnabled()) {
      log.trace("Text after decodeHTML: " + text);
    }

    return text;

  }

  private String decodeHtml(String text) {
    for (Map.Entry<String,String> entry : escapeMap.entrySet()) {
      text = text.replaceAll(entry.getKey(), entry.getValue());
    }
    return text;
  }

  public HtmlMessage createHtmlMessage(DomainProfile domainProfile, Notification notification, Task task, String templatePath) {
    String htmlContent = createHtmlContent(domainProfile, notification, task, templatePath);
    return new HtmlMessage(htmlContent);
  }

  public String createHtmlContent(DomainProfile domainProfile, Notification notification, Task task, String templatePath) {
    Context context = new Context();
    context.setVariable("it", new MessageModel(domainProfile, notification, task));
    return thymeleafFactory.process(templatePath, context);
  }

  public String getEmailTemplatePath(Map<String,String> argMap, String propertyName) {
    return getTemplatePath(argMap, propertyName, "classpath:/notify-kernel/default-email-template.html");
  }

  public String getTemplatePath(Map<String,String> argMap, String propertyName, String defaultPath) {
    String templatePath = argMap.get(propertyName);
    if (StringUtils.isBlank(templatePath)) {
      templatePath = defaultPath;
    }
    return templatePath;
  }

  private static Map<String,String> escapeMap = new HashMap<String,String>();
  private static void register(String utf, String code) {
    escapeMap.put("&" + code + ";", utf);
  }
  static {
    register("\"", "quot"); // " - double-quote
    register("&", "amp"); // & - ampersand
    register("<", "lt"); // < - less-than
    register(">", "gt"); // > - greater-than
    register("\u00A0","nbsp"); // non-breaking space
    register("\u00A1","iexcl"); // inverted exclamation mark
    register("\u00A2","cent"); // cent sign
    register("\u00A3","pound"); // pound sign
    register("\u00A4","curren"); // currency sign
    register("\u00A5","yen"); // yen sign = yuan sign
    register("\u00A6","brvbar"); // broken bar = broken vertical bar
    register("\u00A7","sect"); // section sign
    register("\u00A8","uml"); // diaeresis = spacing diaeresis
    register("\u00A9","copy"); // copyright sign
    register("\u00AA","ordf"); // feminine ordinal indicator
    register("\u00AB","laquo"); // left-pointing double angle quotation mark = left pointing guillemet
    register("\u00AC","not"); // not sign
    register("\u00AD","shy"); // soft hyphen = discretionary hyphen
    register("\u00AE","reg"); // registered trademark sign
    register("\u00AF","macr"); // macron = spacing macron = overline = APL overbar
    register("\u00B0","deg"); // degree sign
    register("\u00B1","plusmn"); // plus-minus sign = plus-or-minus sign
    register("\u00B2","sup2"); // superscript two = superscript digit two = squared
    register("\u00B3","sup3"); // superscript three = superscript digit three = cubed
    register("\u00B4","acute"); // acute accent = spacing acute
    register("\u00B5","micro"); // micro sign
    register("\u00B6","para"); // pilcrow sign = paragraph sign
    register("\u00B7","middot"); // middle dot = Georgian comma = Greek middle dot
    register("\u00B8","cedil"); // cedilla = spacing cedilla
    register("\u00B9","sup1"); // superscript one = superscript digit one
    register("\u00BA","ordm"); // masculine ordinal indicator
    register("\u00BB","raquo"); // right-pointing double angle quotation mark = right pointing guillemet
    register("\u00BC","frac14"); // vulgar fraction one quarter = fraction one quarter
    register("\u00BD","frac12"); // vulgar fraction one half = fraction one half
    register("\u00BE","frac34"); // vulgar fraction three quarters = fraction three quarters
    register("\u00BF","iquest"); // inverted question mark = turned question mark
    register("\u00C0","Agrave"); // ? - uppercase A, grave accent
    register("\u00C1","Aacute"); // ? - uppercase A, acute accent
    register("\u00C2","Acirc"); // ? - uppercase A, circumflex accent
    register("\u00C3","Atilde"); // ? - uppercase A, tilde
    register("\u00C4","Auml"); // ? - uppercase A, umlaut
    register("\u00C5","Aring"); // ? - uppercase A, ring
    register("\u00C6","AElig"); // ? - uppercase AE
    register("\u00C7","Ccedil"); // ? - uppercase C, cedilla
    register("\u00C8","Egrave"); // ? - uppercase E, grave accent
    register("\u00C9","Eacute"); // ? - uppercase E, acute accent
    register("\u00CA","Ecirc"); // ? - uppercase E, circumflex accent
    register("\u00CB","Euml"); // ? - uppercase E, umlaut
    register("\u00CC","Igrave"); // ? - uppercase I, grave accent
    register("\u00CD","Iacute"); // ? - uppercase I, acute accent
    register("\u00CE","Icirc"); // ? - uppercase I, circumflex accent
    register("\u00CF","Iuml"); // ? - uppercase I, umlaut
    register("\u00D0","ETH"); // ? - uppercase Eth, Icelandic
    register("\u00D1","Ntilde"); // ? - uppercase N, tilde
    register("\u00D2","Ograve"); // ? - uppercase O, grave accent
    register("\u00D3","Oacute"); // ? - uppercase O, acute accent
    register("\u00D4","Ocirc"); // ? - uppercase O, circumflex accent
    register("\u00D5","Otilde"); // ? - uppercase O, tilde
    register("\u00D6","Ouml"); // ? - uppercase O, umlaut
    register("\u00D7","times"); // multiplication sign
    register("\u00D8","Oslash"); // ? - uppercase O, slash
    register("\u00D9","Ugrave"); // ? - uppercase U, grave accent
    register("\u00DA","Uacute"); // ? - uppercase U, acute accent
    register("\u00DB","Ucirc"); // ? - uppercase U, circumflex accent
    register("\u00DC","Uuml"); // ? - uppercase U, umlaut
    register("\u00DD","Yacute"); // ? - uppercase Y, acute accent
    register("\u00DE","THORN"); // ? - uppercase THORN, Icelandic
    register("\u00DF","szlig"); // ? - lowercase sharps, German
    register("\u00E0","agrave"); // ? - lowercase a, grave accent
    register("\u00E1","aacute"); // ? - lowercase a, acute accent
    register("\u00E2","acirc"); // ? - lowercase a, circumflex accent
    register("\u00E3","atilde"); // ? - lowercase a, tilde
    register("\u00E4","auml"); // ? - lowercase a, umlaut
    register("\u00E5","aring"); // ? - lowercase a, ring
    register("\u00E6","aelig"); // ? - lowercase ae
    register("\u00E7","ccedil"); // ? - lowercase c, cedilla
    register("\u00E8","egrave"); // ? - lowercase e, grave accent
    register("\u00E9","eacute"); // ? - lowercase e, acute accent
    register("\u00EA","ecirc"); // ? - lowercase e, circumflex accent
    register("\u00EB","euml"); // ? - lowercase e, umlaut
    register("\u00EC","igrave"); // ? - lowercase i, grave accent
    register("\u00ED","iacute"); // ? - lowercase i, acute accent
    register("\u00EE","icirc"); // ? - lowercase i, circumflex accent
    register("\u00EF","iuml"); // ? - lowercase i, umlaut
    register("\u00F0","eth"); // ? - lowercase eth, Icelandic
    register("\u00F1","ntilde"); // ? - lowercase n, tilde
    register("\u00F2","ograve"); // ? - lowercase o, grave accent
    register("\u00F3","oacute"); // ? - lowercase o, acute accent
    register("\u00F4","ocirc"); // ? - lowercase o, circumflex accent
    register("\u00F5","otilde"); // ? - lowercase o, tilde
    register("\u00F6","ouml"); // ? - lowercase o, umlaut
    register("\u00F7","divide"); // division sign
    register("\u00F8","oslash"); // ? - lowercase o, slash
    register("\u00F9","ugrave"); // ? - lowercase u, grave accent
    register("\u00FA","uacute"); // ? - lowercase u, acute accent
    register("\u00FB","ucirc"); // ? - lowercase u, circumflex accent
    register("\u00FC","uuml"); // ? - lowercase u, umlaut
    register("\u00FD","yacute"); // ? - lowercase y, acute accent
    register("\u00FE","thorn"); // ? - lowercase thorn, Icelandic
    register("\u00FF","yuml"); // ? - lowercase y, umlaut
  }
}

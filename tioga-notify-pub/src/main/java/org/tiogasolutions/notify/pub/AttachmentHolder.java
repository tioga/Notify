package org.tiogasolutions.notify.pub;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 5:28 PM
 */
public class AttachmentHolder {
    private final String name;
    private final String contentType;
    private final byte[] content;

    public AttachmentHolder(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() { return content;}
}

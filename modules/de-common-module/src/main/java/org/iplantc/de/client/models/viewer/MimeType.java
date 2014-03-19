/**
 *
 */
package org.iplantc.de.client.models.viewer;

/**
 * @author sriram
 * 
 */
public enum MimeType {

    PNG("png"),

    GIF("gif"),

    JPEG("jpeg"),

    PDF("pdf"),

    PLAIN("plain"),

    HTML("html"),

    XHTML_XML("xhtml+xml"),

    PREVIEW("preview"),

    X_SH("x-sh"),

    VIZ("viz");

    private String type;

    private MimeType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return toString().toLowerCase();
    }

    /**
     * Null-safe and case insensitive variant of valueOf(String)
     * 
     * @param typeString name of an mime type constant
     * @return
     */
    public static MimeType fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }

        String[] tokens = typeString.split("/");
        try {
            if (tokens.length > 1) {
                return valueOf(tokens[1].toUpperCase().replaceAll("[-.+]", "_"));
            } else {
                return valueOf(tokens[0].toUpperCase().replaceAll("[-.+]", "_"));
            }
        } catch (Exception e) {
            return PLAIN;
        }
    }

    @Override
    public String toString() {
        return type;
    }

}

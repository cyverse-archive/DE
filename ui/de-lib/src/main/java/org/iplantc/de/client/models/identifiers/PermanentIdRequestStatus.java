package org.iplantc.de.client.models.identifiers;

/**
 * 
 * @author sriram
 * 
 */
public enum PermanentIdRequestStatus {
    Submitted("Submitted"),
    Evaluation("Evaluation"),
    Validation("Validation"),
    Completion("Completion"),
    Failed("Failed");

    private String displayText;

    PermanentIdRequestStatus(String displayText) {
        this.displayText = displayText;
    }

    public static PermanentIdRequestStatus fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        String temp = typeString.replaceAll("\\s", "");
        return valueOf(temp);
    }


    @Override
    public String toString() {
        return displayText;
    }
}

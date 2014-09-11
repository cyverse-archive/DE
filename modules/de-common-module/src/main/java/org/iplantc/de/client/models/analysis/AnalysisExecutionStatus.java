/**
 * 
 */
package org.iplantc.de.client.models.analysis;


/**
 * @author sriram
 * 
 */
/**
 * Indicates the status of an analysis.
 */
public enum AnalysisExecutionStatus {
    /** analysis status unknown */
    UNKNOWN("Unexpanded"),
    /** analysis is ready */
    SUBMITTED("Submitted"),
    /** analysis is running */
    RUNNING("Running"),
    /** analysis is complete */
    COMPLETED("Completed"),
    /** analysis timed out */
    HELD("Held"),
    /** analysis failed */
    FAILED("Failed"),
    /** analysis was stopped */
    SUBMISSION_ERR("Submission_err"),
    /** analysis is idle */
    IDLE("Idle"),
    /** analysis is removed */
    REMOVED("Removed"),
    /** analyses is cancelled */
    CANCELED("Canceled");

    private String displayText;

    private AnalysisExecutionStatus(String displaytext) {
        this.displayText = displaytext;
    }

    /**
     * Returns a string that identifies the EXECUTION_STATUS.
     * 
     * @return
     */
    public String getTypeString() {
        return toString().toLowerCase();
    }

    /**
     * Null-safe and case insensitive variant of valueOf(String)
     * 
     * @param typeString name of an EXECUTION_STATUS constant
     * @return
     */
    public static AnalysisExecutionStatus fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }

        return valueOf(typeString.toUpperCase());
    }

    @Override
    public String toString() {
        return displayText;
    }
}

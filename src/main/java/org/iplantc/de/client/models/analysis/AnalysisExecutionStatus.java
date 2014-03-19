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
    UNKNOWN(AnalysisExecutionStatusConstants.INSTANCE.unknown()),
    /** analysis is ready */
    SUBMITTED(AnalysisExecutionStatusConstants.INSTANCE.submitted()),
    /** analysis is running */
    RUNNING(AnalysisExecutionStatusConstants.INSTANCE.running()),
    /** analysis is complete */
    COMPLETED(AnalysisExecutionStatusConstants.INSTANCE.completed()),
    /** analysis timed out */
    HELD(AnalysisExecutionStatusConstants.INSTANCE.held()),
    /** analysis failed */
    FAILED(AnalysisExecutionStatusConstants.INSTANCE.failed()),
    /** analysis was stopped */
    SUBMISSION_ERR(AnalysisExecutionStatusConstants.INSTANCE.subErr()),
    /** analysis is idle */
    IDLE(AnalysisExecutionStatusConstants.INSTANCE.idle()),
    /** analysis is removed */
    REMOVED(AnalysisExecutionStatusConstants.INSTANCE.removed());

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

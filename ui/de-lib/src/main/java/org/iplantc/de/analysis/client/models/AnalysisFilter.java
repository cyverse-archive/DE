package org.iplantc.de.analysis.client.models;

/**
 * Created by sriram on 3/4/16.
 */
public enum AnalysisFilter {

    ALL("All"), SHARED_WITH_ME("Analyses shared with me"), MY_ANALYSES("Only my analyses");

    private String filter;

    private AnalysisFilter(String label) {
           this.filter = label;
    }

    public String getFilterString() {
        return toString();
    }

    @Override
    public String toString() {
        return filter;
    }
}

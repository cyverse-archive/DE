package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.resources.client.messages.I18N;

public enum DataSourceEnum {
    file(I18N.APPS_LABELS.fileOutputSrcFileLabel()),
    stdout(I18N.APPS_LABELS.fileOutputSrcStdoutLabel()),
    stderr(I18N.APPS_LABELS.fileOutputSrcStderrLabel());

    private String label;

    DataSourceEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

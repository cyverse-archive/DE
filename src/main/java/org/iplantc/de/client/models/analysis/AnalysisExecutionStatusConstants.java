package org.iplantc.de.client.models.analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface AnalysisExecutionStatusConstants extends Constants {

    static final AnalysisExecutionStatusConstants INSTANCE = GWT.create(AnalysisExecutionStatusConstants.class);

    String unknown();

    String submitted();

    String running();

    String completed();

    String held();

    String failed();

    String subErr();

    String idle();

    String removed();
}

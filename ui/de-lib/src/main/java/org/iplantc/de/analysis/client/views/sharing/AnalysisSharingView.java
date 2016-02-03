/**
 * 
 * @author sriram
 * 
 */
package org.iplantc.de.analysis.client.views.sharing;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.sharing.SharingPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface AnalysisSharingView extends IsWidget {
    void addShareWidget(Widget widget);

    void setPresenter(SharingPresenter sharingPresenter);

    void setSelectedAnalysis(List<Analysis> models);
}

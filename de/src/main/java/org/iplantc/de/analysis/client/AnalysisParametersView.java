package org.iplantc.de.analysis.client;

import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisParamValueSelectedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * Created by jstroot on 2/23/15.
 * @author jstroot
 */
public interface AnalysisParametersView extends IsWidget,
                                                IsMaskable,
                                                SaveAnalysisParametersEvent.HasSaveAnalysisParametersEventHandlers,
                                                AnalysisParamValueSelectedEvent.HasAnalysisParamValueSelectedEventHandlers {

    interface Appearance {

        String diskResourceDoesNotExist(String name);

        String fileUploadSuccess(String name);

        String importFailed(String path);

        String importRequestSubmit(String name);

        String retrieveParametersLoadingMask();

        String viewParameters(String name);
    }

    interface Presenter {

        interface BeanFactory extends AutoBeanFactory {
            AutoBean<File> file();
        }

        void fetchAnalysisParameters(Analysis analysis);

        AnalysisParametersView getView();
    }
}

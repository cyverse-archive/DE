package org.iplantc.de.analysis.client;

import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent;
import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.List;

/**
 *
 * @author sriram
 * 
 */
@SuppressWarnings("rawtypes")
public interface AnalysesView extends
                             IsWidget,
                             SelectionChangedEvent.HasSelectionChangedHandlers,
                             AnalysisParamValueSelectedEvent.HasAnalysisParamValueSelectedEventHandlers,
                             AnalysisAppSelectedEvent.HasAnalysisAppSelectedEventHandlers,
                             AnalysisNameSelectedEvent.HasAnalysisNameSelectedEventHandlers,
                             HTAnalysisExpandEvent.HasHTAnalysisExpandEventHandlers {

    interface Appearance {

        String analysesExecDeleteWarning();

        String appName();

        String endDate();

        String gridEmptyText();

        String name();

        String noParameters();

        String pagingToolbarStyle();

        String paramName();

        String paramType();

        String paramValue();

        String rename();

        String renameAnalysis();

        String retrieveParametersLoadingMask();

        String searchFieldEmptyText();

        String selectionCount(int count);

        String startDate();

        String status();

        String viewParameters(String name);

        String goToOutputFolder();

        ImageResource folderIcon();

        String viewParamLbl();

        ImageResource fileViewIcon();

        String relaunchAnalysis();

        ImageResource runIcon();

        String cancelAnalysis();

        ImageResource deleteIcon();

        String delete();

        ImageResource cancelIcon();

        String analysesMenuLbl();

        String editMenuLbl();

        String renameMenuItem();

        ImageResource fileRenameIcon();

        String updateComments();

        ImageResource userCommentIcon();

        String refresh();

        ImageResource refreshIcon();

        String showAll();

        ImageResource arrow_undoIcon();

        ImageResource saveIcon();

        String saveAs();

        String warning();
    }

    public interface Presenter extends SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler {

        interface Appearance {

            String analysesExecDeleteWarning();

            String analysesRetrievalFailure();

            SafeHtml analysisCommentUpdateFailed();

            SafeHtml analysisCommentUpdateSuccess();

            SafeHtml analysisRenameFailed();

            SafeHtml analysisRenameSuccess();

            String analysisStopSuccess(String name);

            String comments();

            String deleteAnalysisError();

            String diskResourceDoesNotExist(String displayValue);

            String fileUploadSuccess(String name);

            String importFailed(String path);

            String importRequestSubmit(String name);

            String rename();

            String renameAnalysis();

            String savingFileMask();

            String stopAnalysisError(String name);

            String warning();
        }

        void cancelSelectedAnalyses();

        void deleteSelectedAnalyses(List<Analysis> currentSelection);

        List<Analysis> getSelectedAnalyses();

        void go(final HasOneWidget container, List<Analysis> selectedAnalyses);

        void goToSelectedAnalysisFolder();

        void relaunchSelectedAnalysis();

        void renameSelectedAnalysis(Analysis selectedAnalysis, String newName);

        void retrieveParameterData(Analysis analysis, AnalysisParamView apv);

        void setSelectedAnalyses(List<Analysis> selectedAnalyses);

        void setViewDebugId(String baseId);

        void updateAnalysisComment(Analysis value, String comment);

        void updateComments();

        void loadAnalyses(boolean resetFilters);

    }

    void filterByAnalysisId(String id, String name);

    void filterByParentAnalysisId(String id);

    public void loadAnalyses(boolean resetFilters);

    public List<Analysis> getSelectedAnalyses();

    public void setSelectedAnalyses(List<Analysis> selectedAnalyses);

    public void removeFromStore(List<Analysis> items);

    public ListStore<Analysis> getListStore();

    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler);

    void viewParams();
}

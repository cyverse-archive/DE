package org.iplantc.de.theme.base.client.analyses;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.core.client.resources.ThemeStyles;

/**
 * @author jstroot
 */
public class AnalysesViewDefaultAppearance implements AnalysesView.Appearance {
    private final AnalysesMessages analysesMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final AnalysesResources resources;

    public interface AnalysesResources extends ClientBundle {
        @Source("arrow_undo.png")
        ImageResource arrow_undo();

        @Source("file_view.gif")
        ImageResource fileView();

        @Source("AnalysisInfoStyle.css")
        AnalysisInfoStyle css();
    }

    public interface AnalysisInfoStyle extends CssResource {
        String row();
    }

    public AnalysesViewDefaultAppearance() {
        this(GWT.<AnalysesMessages> create(AnalysesMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<AnalysesResources> create(AnalysesResources.class));
   
    }

    AnalysesViewDefaultAppearance(final AnalysesMessages analysesMessages,
                                  final IplantDisplayStrings iplantDisplayStrings,
                                  final IplantResources iplantResources,
                                  final AnalysesResources resources) {
        this.analysesMessages = analysesMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.resources = resources;
        resources.css().ensureInjected();
    }

    @Override
    public AnalysisInfoStyle css() {
        return resources.css();
   }
    @Override
    public String analysesExecDeleteWarning() {
        return analysesMessages.analysesExecDeleteWarning();
    }

    @Override
    public String appName() {
        return analysesMessages.appName();
    }

    @Override
    public String endDate() {
        return analysesMessages.endDate();
    }

    @Override
    public String gridEmptyText() {
        return analysesMessages.gridEmptyText();
    }

    @Override
    public String name() {
        return iplantDisplayStrings.name();
    }

    @Override
    public String noParameters() {
        return analysesMessages.noParameters();
    }

    @Override
    public String pagingToolbarStyle() {
        return ThemeStyles.get().style().borderTop();
    }

    @Override
    public String paramName() {
        return analysesMessages.paramName();
    }

    @Override
    public String paramType() {
        return analysesMessages.paramType();
    }

    @Override
    public String paramValue() {
        return iplantDisplayStrings.paramValue();
    }

    @Override
    public String rename() {
        return iplantDisplayStrings.rename();
    }

    @Override
    public String renameAnalysis() {
        return analysesMessages.renameAnalysis();
    }

    @Override
    public String retrieveParametersLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String searchFieldEmptyText() {
        return analysesMessages.searchFieldEmptyText();
    }

    @Override
    public String selectionCount(int count) {
        return analysesMessages.selectionCount(count);
    }

    @Override
    public String startDate() {
        return analysesMessages.startDate();
    }

    @Override
    public String status() {
        return iplantDisplayStrings.status();
    }

    @Override
    public String goToOutputFolder() {
        return analysesMessages.goToOutputFolder();
    }

    @Override
    public ImageResource folderIcon() {
        return iplantResources.folder();
    }

    @Override
    public String viewParamLbl() {
        return analysesMessages.viewParamLbl();
    }

    @Override
    public ImageResource fileViewIcon() {
        return resources.fileView();
    }

    @Override
    public String relaunchAnalysis() {
        return analysesMessages.relaunchAnalysis();
    }

    @Override
    public ImageResource runIcon() {
        return iplantResources.run();
    }

    @Override
    public String cancelAnalysis() {
        return analysesMessages.cancelAnalysis();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.deleteIcon();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource cancelIcon() {
        return iplantResources.cancel();
    }

    @Override
    public String analysesMenuLbl() {
        return analysesMessages.analysesMenuLbl();
    }

    @Override
    public String editMenuLbl() {
        return iplantDisplayStrings.edit();
    }

    @Override
    public String renameMenuItem() {
        return analysesMessages.renameMenuItem();
    }

    @Override
    public ImageResource fileRenameIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String updateComments() {
        return analysesMessages.updateComments();
    }

    @Override
    public ImageResource userCommentIcon() {
        return iplantResources.userComment();
    }

    @Override
    public String refresh() {
        return iplantDisplayStrings.refresh();
    }

    @Override
    public ImageResource refreshIcon() {
        return iplantResources.refresh();
    }

    @Override
    public String showAll() {
        return analysesMessages.showAll();
    }

    @Override
    public ImageResource arrow_undoIcon() {
        return resources.arrow_undo();
    }

    @Override
    public ImageResource saveIcon() {
        return iplantResources.save();
    }

    @Override
    public String saveAs() {
        return iplantDisplayStrings.saveAs();
    }

    @Override
    public String warning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String viewAnalysisStepInfo() {
        return analysesMessages.viewAnalysisStepInfo();
    }

    @Override
    public String stepType() {
        return analysesMessages.stepType();
    }

    @Override
    public String jobId() {
        return analysesMessages.jobId();
    }

    @Override
    public ImageResource shareIcon() {
        return iplantResources.share();
    }

    @Override
    public String share() {
        return analysesMessages.share();
    }

    @Override
    public String shareCollab() {
        return analysesMessages.shareCollab();
    }

    @Override
    public String shareSupport() {
        return analysesMessages.shareSupportMi();
    }

    @Override
    public String shareSupportConfirm() {
        return analysesMessages.shareSupportConfirm();
    }

    @Override
    public String shareWithInput() {
        return analysesMessages.shareWithInput();
    }

    @Override
    public String shareOutputOnly() {
        return analysesMessages.shareOutputOnly();
    }
}

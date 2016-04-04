package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.PipelineEditorWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.pipelines.client.presenter.PipelineViewPresenter;
import org.iplantc.de.pipelines.client.views.PipelineView;
import org.iplantc.de.pipelines.client.views.PipelineViewImpl;
import org.iplantc.de.pipelines.shared.Pipelines;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

/**
 * @author jstroot
 */
public class PipelineEditorWindow extends IplantWindowBase {
    class PublishCallbackCommand implements Command {
        @Override
        public void execute() {
            announcer.schedule(new SuccessAnnouncementConfig(displayStrings.publishWorkflowSuccess()));
            if (close_after_save) {
                close_after_save = false;
                PipelineEditorWindow.super.hide();
            } else {
                initPipelineJson = presenter.getPublishJson(presenter.getPipeline());
            }
        }
    }

    @Inject IplantAnnouncer announcer;

    private final IplantDisplayStrings displayStrings;
    @Inject AppsView.Presenter appsViewPresenter;
    private PipelineView.Presenter presenter;
    private boolean close_after_save;
    private String initPipelineJson;
    private PipelineView view;

    @Inject
    PipelineEditorWindow(final IplantDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;

        setHeadingText(displayStrings.pipeline());
        setSize("900", "500");
        setMinWidth(640);
        setMinHeight(440);
    }

    @Override
    public <C extends WindowConfig> void show(C windowConfig, String tag,
                                              boolean isMaximizable) {

        this.view = new PipelineViewImpl();
        presenter = new PipelineViewPresenter(view, new PublishCallbackCommand(), appsViewPresenter);

        if (windowConfig instanceof PipelineEditorWindowConfig) {
            PipelineEditorWindowConfig pipelineConfig = (PipelineEditorWindowConfig) windowConfig;
            Pipeline pipeline = pipelineConfig.getPipeline();

            if (pipeline != null) {
                presenter.setPipeline(pipeline);
                initPipelineJson = presenter.getPublishJson(pipeline);
            } else {
                Splittable serviceWorkflowJson = pipelineConfig.getServiceWorkflowJson();
                if (serviceWorkflowJson != null) {
                    initPipelineJson = serviceWorkflowJson.getPayload();
                }
                presenter.setPipeline(serviceWorkflowJson);
            }

        }

        presenter.go(this);
        close_after_save = false;
        super.show(windowConfig, tag, isMaximizable);

        ensureDebugId(DeModule.WindowIds.WORKFLOW_EDITOR);
    }

    @Override
    public WindowState getWindowState() {
        PipelineEditorWindowConfig configData = ConfigFactory.workflowIntegrationWindowConfig();
        configData.setPipeline(presenter.getPipeline());
        return createWindowState(configData);
    }

    @Override
    public void hide() {
        if (!isMinimized()) {
            if (initPipelineJson != null
                    && !initPipelineJson.equals(presenter.getPublishJson(presenter.getPipeline()))) {
                checkForSave();
            } else if (initPipelineJson == null
                           && presenter.getPublishJson(presenter.getPipeline()) != null) {
                checkForSave();
            } else {
                PipelineEditorWindow.super.hide();
            }
        } else {
            PipelineEditorWindow.super.hide();
        }
    }

    private void checkForSave() {
        MessageBox box = new MessageBox(displayStrings.save(), "");
        box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
        box.setIcon(MessageBox.ICONS.question());
        box.setMessage(displayStrings.unsavedChanges());
        box.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {

                if (PredefinedButton.NO.equals(event.getHideButton())) {
                    PipelineEditorWindow.super.hide();
                } else if (PredefinedButton.YES.equals(event.getHideButton())) {
                    presenter.saveOnClose();
                    close_after_save = true;
                }
            }
        });
        box.show();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        view.asWidget().ensureDebugId(baseID + Pipelines.Ids.WORKFLOW_VIEW);
    }

}

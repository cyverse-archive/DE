package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.PipelineEditorWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.pipelines.client.presenter.PipelineViewPresenter;
import org.iplantc.de.pipelines.client.views.PipelineView;
import org.iplantc.de.pipelines.client.views.PipelineViewImpl;

import com.google.gwt.user.client.Command;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class PipelineEditorWindow extends IplantWindowBase {
    private final PipelineView.Presenter presenter;
    private String initPipelineJson;
    private boolean close_after_save;

    public PipelineEditorWindow(WindowConfig config) {
        super(null, null);

        setHeadingText(org.iplantc.de.resources.client.messages.I18N.DISPLAY.pipeline());
        setSize("900", "500"); //$NON-NLS-1$ //$NON-NLS-2$
        setMinWidth(640);
        setMinHeight(440);

        PipelineView view = new PipelineViewImpl();
        presenter = new PipelineViewPresenter(view, new PublishCallbackCommand());

        if (config instanceof PipelineEditorWindowConfig) {
            PipelineEditorWindowConfig pipelineConfig = (PipelineEditorWindowConfig)config;
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
    }

    class PublishCallbackCommand implements Command {
        @Override
        public void execute() {
            IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(org.iplantc.de.resources.client.messages.I18N.DISPLAY.publishWorkflowSuccess()));
            if (close_after_save) {
                close_after_save = false;
                PipelineEditorWindow.super.hide();
            } else {
                initPipelineJson = presenter.getPublishJson(presenter.getPipeline());
            }
        }
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
        MessageBox box = new MessageBox(org.iplantc.de.resources.client.messages.I18N.DISPLAY.save(), "");
        box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
        box.setIcon(MessageBox.ICONS.question());
        box.setMessage(org.iplantc.de.resources.client.messages.I18N.DISPLAY.unsavedChanges());
        box.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                Dialog btn = (Dialog)event.getSource();
                if (btn.getHideButton().getText().equalsIgnoreCase(PredefinedButton.NO.toString())) {
                    PipelineEditorWindow.super.hide();
                }
                if (btn.getHideButton().getText().equalsIgnoreCase(PredefinedButton.YES.toString())) {
                    presenter.saveOnClose();
                    close_after_save = true;
                }

            }
        });
        box.show();
    }

    @Override
    public WindowState getWindowState() {
        PipelineEditorWindowConfig configData = ConfigFactory.workflowIntegrationWindowConfig();
        configData.setPipeline(presenter.getPipeline());
        return createWindowState(configData);
    }
}

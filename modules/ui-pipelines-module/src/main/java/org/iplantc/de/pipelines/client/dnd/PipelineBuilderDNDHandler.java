package org.iplantc.de.pipelines.client.dnd;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.DndDragCancelHandler;
import com.sencha.gxt.dnd.core.client.StatusProxy;

import java.util.List;

/**
 * A common Handler for drag'n'drop events from the Apps grid to the PipelineBuilder.
 *
 * @author psarando
 *
 */
public abstract class PipelineBuilderDNDHandler implements DndDragCancelHandler {
    public interface Presenter {
        void addAppToPipeline(App app);

        void maskPipelineBuilder(String message);

        void unmaskPipelineBuilder();
    }

    protected Presenter presenter;

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    protected void validateDNDEvent(StatusProxy eventStatus, List<App> selected) {
        if (eventStatus == null) {
            return;
        }

        if (selected == null || selected.isEmpty()) {
            eventStatus.setStatus(false);
            return;
        }

        for (App app : selected) {
            if (!app.getPipelineEligibility().isValid()) {
                eventStatus.setStatus(false);
                eventStatus.update(app.getPipelineEligibility().getReason());
                return;
            }

            eventStatus.update(I18N.DISPLAY.appendAppToWorkflow(app.getName()));
        }
    }

    @Override
    public void onDragCancel(DndDragCancelEvent event) {
        presenter.unmaskPipelineBuilder();
    }
}

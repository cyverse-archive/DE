package org.iplantc.de.pipelines.client.dnd;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.StatusProxy;

import java.util.List;

/**
 * A PipelineBuilderDNDHandler for dropping Apps onto the PipelineCreator panel.
 *
 * @author psarando
 *
 */
public class PipelineBuilderDropHandler extends PipelineBuilderDNDHandler implements
        DndDragEnterHandler, DndDragLeaveHandler, DndDropHandler {

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        StatusProxy eventStatus = event.getStatusProxy();
        @SuppressWarnings("unchecked")
        List<App> selected = (List<App>)event.getDragSource().getData();
        validateDNDEvent(eventStatus, selected);

        if (eventStatus.getStatus()) {
            // Event Status is true, so we have a valid App
            String appName = selected.get(0).getName();
            presenter.maskPipelineBuilder(I18N.DISPLAY.appendAppToWorkflow(appName));
        }
    }

    @Override
    public void onDragLeave(DndDragLeaveEvent event) {
        presenter.unmaskPipelineBuilder();
    }

    @Override
    public void onDrop(DndDropEvent event) {
        @SuppressWarnings("unchecked")
        List<App> data = (List<App>)event.getData();

        if (data == null || data.isEmpty()) {
            return;
        }

        for (final App app : data) {
            presenter.addAppToPipeline(app);
        }
    }
}

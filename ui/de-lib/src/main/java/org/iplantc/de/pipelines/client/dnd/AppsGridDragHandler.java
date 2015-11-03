package org.iplantc.de.pipelines.client.dnd;

import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.StatusProxy;

import java.util.List;

/**
 * A PipelineBuilderDNDHandler for dragging Apps from the Apps Grid.
 * 
 * @author psarando
 * 
 */
public class AppsGridDragHandler extends PipelineBuilderDNDHandler implements DndDragStartHandler {

    @Override
    public void onDragStart(DndDragStartEvent event) {
        @SuppressWarnings("unchecked")
        List<App> selected = (List<App>)event.getData();
        StatusProxy eventStatus = event.getStatusProxy();

        validateDNDEvent(eventStatus, selected);
    }
}

/**
 *
 */
package org.iplantc.de.fileViewers.client.views.cells;

import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.commons.client.util.WindowUtil;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author sriram, jstroot
 */
public class TreeUrlCell extends AbstractCell<VizUrl> {

    public interface TreeUrlCellAppearance {
        void render(SafeHtmlBuilder sb, VizUrl model);

        String treeUrlExternalWindowWidthHeight();
    }

    private final TreeUrlCellAppearance appearance = GWT.create(TreeUrlCellAppearance.class);

    public TreeUrlCell() {
        super(CLICK);
    }

    @Override
    public void onBrowserEvent(Context context, Element parent,
                               VizUrl value, NativeEvent event, ValueUpdater<VizUrl> valueUpdater) {
        if (value == null) {
            return;
        }
        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        WindowUtil.open(value.getUrl(), appearance.treeUrlExternalWindowWidthHeight());
    }

    @Override
    public void render(Context context, VizUrl model,
                       SafeHtmlBuilder sb) {
        appearance.render(sb, model);
    }

}

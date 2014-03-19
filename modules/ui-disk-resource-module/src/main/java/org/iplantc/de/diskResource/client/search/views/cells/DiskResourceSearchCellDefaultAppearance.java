package org.iplantc.de.diskResource.client.search.views.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.theme.base.client.field.DateCellDefaultAppearance;
import com.sencha.gxt.theme.base.client.field.TriggerFieldDefaultAppearance;

/**
 * This class is a clone-and-own of {@link DateCellDefaultAppearance}.
 * 
 * @author jstroot
 * 
 */
public class DiskResourceSearchCellDefaultAppearance extends TriggerFieldDefaultAppearance implements DiskResourceSearchCell.DiskResourceSearchCellAppearance {

    public interface DiskResourceSearchCellResources extends TriggerFieldResources {

        @Override
        @Source({"DiskResourceSearchField.css", "com/sencha/gxt/theme/base/client/field/ValueBaseField.css", "com/sencha/gxt/theme/base/client/field/TextField.css",
                "com/sencha/gxt/theme/base/client/field/TriggerField.css"})
        DiskResourceSearchCellStyle css();
        
        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrow();

        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrowOver();

        /*
         * @Override
         * 
         * @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
         * ImageResource textBackground();
         */

        // TODO Override images
        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrowClick();

        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrowFocus();

        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrowFocusOver();

        @Override
        @Source("funnel-icon.png")
        ImageResource triggerArrowFocusClick();
    }

    public interface DiskResourceSearchCellStyle extends TriggerFieldStyle {

    }

    public DiskResourceSearchCellDefaultAppearance() {
        this(GWT.<DiskResourceSearchCellResources> create(DiskResourceSearchCellResources.class));
    }

    public DiskResourceSearchCellDefaultAppearance(DiskResourceSearchCellResources resources) {
        super(resources);
    }

}

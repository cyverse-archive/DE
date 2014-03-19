package org.iplantc.de.diskResource.client.search.views.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.theme.base.client.menu.MenuBaseAppearance;

/**
 * This class is necessary in order to override the background image in the {@link MenuStyle#menu()} css
 * class.
 * 
 * @author jstroot
 * 
 */
class DiskResourceQueryFormMenuAppearance extends MenuBaseAppearance {

    public interface DiskResourceQueryFormMenuResources extends MenuBaseAppearance.MenuResources, ClientBundle {

        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Vertical)
        @Source("com/sencha/gxt/theme/gray/client/menu/itemOver.gif")
        ImageResource itemOver();

        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Vertical)
        @Source("com/sencha/gxt/theme/gray/client/menu/menu.gif")
        ImageResource menu();

        @Source("com/sencha/gxt/theme/gray/client/menu/miniBottom.gif")
        ImageResource miniBottom();

        @Source("com/sencha/gxt/theme/gray/client/menu/miniTop.gif")
        ImageResource miniTop();

        @Override
        @Source({"com/sencha/gxt/theme/base/client/menu/Menu.css", "DiskResourceQueryFormMenu.css"})
        DiskResourceQueryFormMenuStyle style();

    }

    public interface DiskResourceQueryFormMenuStyle extends MenuStyle {
    }

    public DiskResourceQueryFormMenuAppearance() {
        this(GWT.<DiskResourceQueryFormMenuResources> create(DiskResourceQueryFormMenuResources.class), GWT.<BaseMenuTemplate> create(BaseMenuTemplate.class));
    }

    public DiskResourceQueryFormMenuAppearance(DiskResourceQueryFormMenuResources resources, BaseMenuTemplate template) {
        super(resources, template);
    }

}

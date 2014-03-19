package org.iplantc.de.resources.client.theme.window;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.gray.client.window.GrayWindowAppearance;

public class IPlantGrayWindowAppearance extends GrayWindowAppearance implements IPlantWindowAppearance {

    public interface IPlantGrayHeaderResources extends GrayHeaderResources {

        @Override
        @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "com/sencha/gxt/theme/gray/client/window/GrayWindowHeader.css", "IPlantGrayWindowHeader.css"})
        GrayHeaderStyle style();

    }

    @Override
    public HeaderDefaultAppearance getHeaderAppearance() {
        return new HeaderDefaultAppearance(GWT.<IPlantGrayHeaderResources> create(IPlantGrayHeaderResources.class));
    }
}

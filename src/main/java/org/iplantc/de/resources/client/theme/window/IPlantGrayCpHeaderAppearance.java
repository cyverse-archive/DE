package org.iplantc.de.resources.client.theme.window;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.gray.client.panel.GrayHeaderAppearance;

public class IPlantGrayCpHeaderAppearance extends GrayHeaderAppearance {

    public interface IPlantGrayHeaderResources extends GrayHeaderResources {

        @Override
        @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "com/sencha/gxt/theme/gray/client/panel/GrayHeader.css", "IPlantGrayWindowHeader.css"})
        GrayHeaderStyle style();

    }

    public IPlantGrayCpHeaderAppearance() {
        super(GWT.<IPlantGrayHeaderResources> create(IPlantGrayHeaderResources.class), GWT.<Template> create(Template.class));
    }
}

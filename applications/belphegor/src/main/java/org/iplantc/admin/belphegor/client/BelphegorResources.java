package org.iplantc.admin.belphegor.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface BelphegorResources extends ClientBundle {

    @Source("BelphegorStyle.css")
    BelphegorStyle css();

    @Source("headerlogo.png")
    ImageResource headerLogo();

    @Source("headerlogo-fill.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource headerLogoFill();
}

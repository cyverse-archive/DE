package org.iplantc.de.commons.client.views.gxt3.dialogs;

import com.sencha.gxt.widget.core.client.box.MessageBox;

public class IplantInfoBox extends MessageBox {


    public IplantInfoBox(String headingHtml, String messageHtml, WindowAppearance appearance, MessageBoxAppearance contentAppearance) {
        super(headingHtml, messageHtml, appearance, contentAppearance);

        setIcon(MessageBox.ICONS.info());
    }

    public IplantInfoBox(String headingHtml, String messageHtml) {
        super(headingHtml, messageHtml);
        setIcon(MessageBox.ICONS.info());
    }

}

package org.iplantc.de.commons.client.widgets;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

import com.sencha.gxt.core.client.dom.XElement;

public interface PushButtonAppearance {

        void render(SafeHtmlBuilder sb);

        void onUpdateText(XElement parent, String text);

        void onUpdateIcon(XElement parent, Image icon);

}

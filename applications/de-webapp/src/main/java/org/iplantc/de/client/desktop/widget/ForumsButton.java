package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ForumsButton extends IconButton {
    public ForumsButton(final DeResources resources, final CommonUiConstants CONSTANTS) {
        super(resources.css().askForums());
        setSize("28", "28");
        setToolTip(I18N.DISPLAY.forums());
        addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                WindowUtil.open(CONSTANTS.forumsUrl());
                
            }
        });
        getElement().setAttribute("data-intro", I18N.TOUR.introAsk());
        getElement().setAttribute("data-position", "left");
        getElement().setAttribute("data-step", "7");
        ensureDebugId(DeModule.Ids.DESKTOP + DeModule.Ids.FORUMS_BUTTON);
        addHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                changeStyle(resources.css().askForumsHover());
            }
        }, MouseOverEvent.getType());

        addHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                changeStyle(resources.css().askForums());
            }
        }, MouseOutEvent.getType());
    }

}

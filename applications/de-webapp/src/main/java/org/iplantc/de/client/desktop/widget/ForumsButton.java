package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ForumsButton extends IconButton {
    public ForumsButton(DeResources resources, final CommonUiConstants CONSTANTS) {
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
        
    }

}

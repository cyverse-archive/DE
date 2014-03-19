package org.iplantc.de.commons.client.views.gxt3.dialogs;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class ErrorDialog3 extends IPlantDialog {

    private static ErrorDialog3UiBinder uiBinder = GWT.create(ErrorDialog3UiBinder.class);

    interface ErrorDialog3UiBinder extends UiBinder<Widget, ErrorDialog3> {
    }

    @UiField
    HTML errorMsg;

//    @UiField
//    TextButton expandDetailsButton;

    @UiField
    FramedPanel detailsPanel;

    @UiField
    TextArea descriptionArea;
    
    @UiField
    VerticalLayoutContainer detailsContainer;

    public ErrorDialog3(SafeHtml errorMsg, String description) {
        
        setHeadingText(I18N.DISPLAY.error());
        this.setMinHeight(300);
        this.setMinWidth(350);
        this.setResizable(false);

        add(uiBinder.createAndBindUi(this));
        detailsPanel.setHeadingText(I18N.DISPLAY.details());
        detailsPanel.setSize("330","150");

        if (errorMsg != null) {
            this.errorMsg.setHTML(errorMsg);
        }
   
        detailsContainer.setScrollMode(ScrollMode.AUTO);
        descriptionArea.setText(description);
        detailsPanel.setCollapsible(true);
        detailsPanel.expand();
    }

    @Override
    protected void init() {
        super.init();
        setPredefinedButtons(PredefinedButton.OK);
    }

//    @UiHandler("expandDetailsButton")
//    void onExpandDetailsClick(SelectEvent event) {
//        detailsPanel.setExpanded(!detailsPanel.isExpanded());
//    }

    @UiHandler("detailsPanel")
    void onExpand(ExpandEvent event) {
    }

    @UiHandler("detailsPanel")
    void onCollapse(CollapseEvent event) {

    }

}

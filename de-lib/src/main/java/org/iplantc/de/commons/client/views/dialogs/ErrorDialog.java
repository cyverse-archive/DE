package org.iplantc.de.commons.client.views.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
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

/**
 * @author jstroot
 */
public class ErrorDialog extends IPlantDialog {

    public interface ErrorDialogAppearance {

        String detailsHeading();

        String detailsPanelHeight();

        String detailsPanelWidth();

        String errorHeading();

        int minHeight();

        int minWidth();

        ImageResource errorIcon();

        String bgColor();
    }

    private static ErrorDialogUiBinder uiBinder = GWT.create(ErrorDialogUiBinder.class);

    interface ErrorDialogUiBinder extends UiBinder<Widget, ErrorDialog> { }

    @UiField(provided = true) ErrorDialogAppearance appearance;
    @UiField HTML errorMsg;
    @UiField FramedPanel detailsPanel;
    @UiField TextArea descriptionArea;
    @UiField VerticalLayoutContainer detailsContainer;

    public ErrorDialog(final SafeHtml errorMsg,
                       final String description) {
        this(errorMsg, description, GWT.<ErrorDialogAppearance> create(ErrorDialogAppearance.class));
    }

    public ErrorDialog(final SafeHtml errorMsg,
                       final String description,
                       final ErrorDialogAppearance appearance) {
        this.appearance = appearance;

        setHeadingText(appearance.errorHeading());
        this.setMinHeight(appearance.minHeight());
        this.setMinWidth(appearance.minWidth());
        this.setResizable(false);

        add(uiBinder.createAndBindUi(this));
        detailsPanel.setHeadingText(appearance.detailsHeading());
        detailsPanel.setSize(appearance.detailsPanelWidth(), appearance.detailsPanelHeight());

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

    @UiHandler("detailsPanel")
    void onExpand(ExpandEvent event) {
    }

    @UiHandler("detailsPanel")
    void onCollapse(CollapseEvent event) {
    }

}

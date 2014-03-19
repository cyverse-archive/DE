package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;

/**
 * A Dialog for displaying the Apps View and adding Apps to pipelines.
 *
 * @author psarando
 *
 */
public class AppSelectionDialog extends Dialog {

    public interface Presenter {
        void onAddAppClick();
    }

    private Presenter presenter;

    private final Status appCountStatus;
    private final Status lastAppStatus;

    public AppSelectionDialog() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CLOSE);
        setHeadingText(I18N.DISPLAY.selectWindowTitle());
        setSize("800", "400"); //$NON-NLS-1$ //$NON-NLS-2$
        setModal(true);
        setAutoHide(false);
        setResizable(false);
        setHideOnButtonClick(false);
        setButtonAlign(BoxLayoutPack.START);

        ButtonBar btnBar = getButtonBar();

        TextButton okBtn = (TextButton)btnBar.getItemByItemId(PredefinedButton.OK.name());
        okBtn.setText(I18N.DISPLAY.add());
        okBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.onAddAppClick();
            }
        });

        appCountStatus = new Status((BoxStatusAppearance)GWT.create(BoxStatusAppearance.class));
        lastAppStatus = new Status((BoxStatusAppearance)GWT.create(BoxStatusAppearance.class));

        btnBar.insert(appCountStatus, 0);
        btnBar.insert(lastAppStatus, 1);
        btnBar.insert(new FillToolItem(), 2);

        updateStatusBar(0, null);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Updates the app count and last app added status label.
     *
     * @param appCount Number of apps added to the workflow.
     * @param lastAppLabel A label describing the app that was added last, or null.
     */
    public void updateStatusBar(int appCount, String lastAppLabel) {
        appCountStatus.setText(appCount == 1 ? I18N.DISPLAY.appCountSingular() : I18N.DISPLAY
                .appCountPlural(appCount));
        lastAppStatus.setText(lastAppLabel == null ? I18N.DISPLAY.lastApp(I18N.DISPLAY
                .lastAppNotDefined()) : I18N.DISPLAY.lastApp(lastAppLabel));
        if(!Strings.isNullOrEmpty(lastAppLabel)) {
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            builder.appendEscaped(lastAppLabel + " added.");
            SuccessAnnouncementConfig config = new SuccessAnnouncementConfig(builder.toSafeHtml(), true);
            IplantAnnouncer.getInstance().schedule(config);
        }
    }
}

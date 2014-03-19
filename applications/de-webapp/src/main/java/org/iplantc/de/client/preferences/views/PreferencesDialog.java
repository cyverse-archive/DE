/**
 * 
 */
package org.iplantc.de.client.preferences.views;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.PreferencesUpdatedEvent;
import org.iplantc.de.client.preferences.presenter.PreferencesPresenterImpl;
import org.iplantc.de.client.preferences.views.PreferencesView.Presenter;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * A dialog to show preferences view
 * 
 * @author sriram
 * 
 */
public class PreferencesDialog extends IPlantDialog {

    private Presenter presenter;

    public interface HtmlLayoutContainerTemplate extends XTemplates {
        @XTemplate(source = "PreferencesHelpTemplate.html")
        SafeHtml getTemplate();
    }

    public PreferencesDialog() {
        super(true);
        setHeadingText(org.iplantc.de.resources.client.messages.I18N.DISPLAY.preferences());
        setPixelSize(450, 400);
        setButtons();
        addHelp(contructHelpView());
        PreferencesView view = new PreferencesViewImpl();
        presenter = new PreferencesPresenterImpl(view);
        presenter.go(this);
    }

    private void setButtons() {
        ButtonBar buttonBar = getButtonBar();
        buttonBar.clear();
        setDefaultsButton();
        setOkButton();
    }

    private Widget contructHelpView() {
        HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);
        HtmlLayoutContainer c = new HtmlLayoutContainer(templates.getTemplate());
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.DISPLAY.notifyemail()), new HtmlData(".emailHeader"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.HELP.notifyemailHelp()), new HtmlData(".emailHelp"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.DISPLAY.rememberFileSectorPath()), new HtmlData(".filePathHeader"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.HELP.rememberFileSectorPathHelp()), new HtmlData(".filePathHelp"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.DISPLAY.saveSession()), new HtmlData(".saveSessionHeader"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.HELP.saveSessionHelp()), new HtmlData(".saveSessionHelp"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.DISPLAY.defaultOutputFolder()), new HtmlData(".defaultOp"));
        c.add(new HTML(org.iplantc.de.resources.client.messages.I18N.HELP.defaultOutputFolderHelp()), new HtmlData(".defaultOpHelp"));
        return c.asWidget();
    }

    private void setOkButton() {
        TextButton ok = new TextButton(org.iplantc.de.resources.client.messages.I18N.DISPLAY.done());
        ok.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (presenter.validateAndSave()) {
                    hide();
                    PreferencesUpdatedEvent pue = new PreferencesUpdatedEvent();
                    EventBus.getInstance().fireEvent(pue);
                } else {
                    ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(org.iplantc.de.resources.client.messages.I18N.DISPLAY
                            .publicSubmitTip());
                    IplantAnnouncer.getInstance().schedule(config);
                }
            }
        });

        ok.setId("idbtnPrefDone");
        getButtonBar().add(ok);
    }

    private void setDefaultsButton() {
        TextButton def = new TextButton(org.iplantc.de.resources.client.messages.I18N.DISPLAY.restoreDefaults());

        def.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.setDefaults();

            }
        });
        def.setId("btn_default");
        getButtonBar().add(def);
    }
}

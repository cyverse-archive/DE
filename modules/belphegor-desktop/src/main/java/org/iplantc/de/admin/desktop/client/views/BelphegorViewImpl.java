package org.iplantc.de.admin.desktop.client.views;

import org.iplantc.de.admin.desktop.client.BelphegorResources;
import org.iplantc.de.admin.desktop.client.BelphegorStyle;
import org.iplantc.de.admin.desktop.client.Constants;
import org.iplantc.de.admin.desktop.client.I18N;
import org.iplantc.de.admin.desktop.client.apps.presenter.BelphegorAppsViewPresenterImpl;
import org.iplantc.de.admin.desktop.client.models.BelphegorAdminProperties;
import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.menu.Menu;

public class BelphegorViewImpl extends Composite implements BelphegorView {

    private static BelphegorViewUiBinder uiBinder = GWT.create(BelphegorViewUiBinder.class);

    @UiTemplate("BelphegorView.ui.xml")
    interface BelphegorViewUiBinder extends UiBinder<Widget, BelphegorViewImpl> {}

    public interface MyTemplate extends XTemplates {
        @XTemplate("<div class='{style.iplantcHeader}'>" + "<table><tbody><tr>"
                + "<td role='presentation' align='LEFT' valign='TOP'><a style='outline-style: none;' href='{iplantHome}' target='_blank'><div class='{style.iplantcLogo}'></div></a></td>"
                + "<td role='presentation' align='LEFT' valign='TOP'><div class='{style.iplantcHeaderMenu}'></div>" 
                + "</td></tr></tbody></table></div>")
        SafeHtml getTemplate(BelphegorStyle style, SafeUri iplantHome);
    }

    @UiField(provided = true) BelphegorResources res;
    @UiField(provided = true) IplantDisplayStrings strings = I18N.DISPLAY;
    @UiField HtmlLayoutContainer northCon;
    @UiField SimpleContainer appsPanel,
        refGenomePanel,
        toolRequestPanel,
        systemMessagesPanel;

    private final MyTemplate template = GWT.create(MyTemplate.class);

    @Inject
    public BelphegorViewImpl(final BelphegorAppsViewPresenterImpl presenter,
                             final RefGenomeView.Presenter refGenPresenter,
                             final ToolRequestView.Presenter toolReqPresenter,
                             final SystemMessageView.Presenter sysMsgPresenter,
                             final BelphegorAdminProperties toolIntProps,
                             final BelphegorResources resources) {
        this.res = resources;
        res.css().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        init(presenter, refGenPresenter, toolReqPresenter, sysMsgPresenter, toolIntProps);
    }

    @UiFactory
    HtmlLayoutContainer buildHtmlLayoutContainer() {
        return new HtmlLayoutContainer(template.getTemplate(res.css(), UriUtils.fromSafeConstant(Constants.CLIENT.iplantHome())));
    }

    private void init(final BelphegorAppsViewPresenterImpl presenter,
                      final RefGenomeView.Presenter refGenPresenter,
                      final ToolRequestView.Presenter toolReqPresenter,
                      final SystemMessageView.Presenter sysMsgPresenter,
                      final BelphegorAdminProperties toolIntProps) {
        buildUserMenu();
        HasId betaGroup = CommonModelUtils.getInstance().createHasIdFromString(toolIntProps.getDefaultBetaAppCategoryId());
        presenter.go(appsPanel, betaGroup, null);
        refGenPresenter.go(refGenomePanel);
        toolReqPresenter.go(toolRequestPanel);
        sysMsgPresenter.go(systemMessagesPanel);
    }

    private void buildUserMenu() {

        Menu userMenu = new Menu();

        userMenu.setBorders(true);
        userMenu.setStyleName(res.css().iplantcHeaderMenuBody());
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.logout(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.Location.assign(GWT.getHostPageBaseURL() + Constants.CLIENT.logoutUrl());
            }
        }));

        String username = UserInfo.getInstance().getUsername();
        String firstName = UserInfo.getInstance().getFirstName();
        String lastName = UserInfo.getInstance().getLastName();
        String menuLabel = (firstName != null && lastName != null) ? firstName + " " + lastName : username;
        TextButton menuButton = new TextButton(menuLabel);
        menuButton.setMenu(userMenu);

        northCon.add(menuButton, new HtmlData("." + res.css().iplantcHeaderMenu()));
    }

}

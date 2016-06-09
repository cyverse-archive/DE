package org.iplantc.de.admin.desktop.client.views;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView;
import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * @author jstroot
 */
public class BelphegorViewImpl extends Composite implements BelphegorView {

    private static BelphegorViewUiBinder uiBinder = GWT.create(BelphegorViewUiBinder.class);

    @UiTemplate("BelphegorView.ui.xml")
    interface BelphegorViewUiBinder extends UiBinder<Widget, BelphegorViewImpl> {}

    @UiField HtmlLayoutContainer northCon;
    @UiField SimpleContainer appsPanel, refGenomePanel, toolRequestPanel, systemMessagesPanel, metadataPanel,
            permIdPanel, toolAdminPanel;
    @UiField(provided = true) BelphegorViewAppearance appearance;
    private TextButton menuButton;
    private AdminAppsView.AdminPresenter presenter;
    private RefGenomeView.Presenter refGenPresenter;
    private ToolRequestView.Presenter toolReqPresenter;
    private ToolAdminView.Presenter toolAdminPresenter;
    private SystemMessageView.Presenter sysMsgPresenter;
    private TemplateListingView.Presenter tempPresenter;
    private PermanentIdRequestView.Presenter permIdPresenter;

    @Inject
    public BelphegorViewImpl(final AdminAppsView.AdminPresenter presenter,
                             final RefGenomeView.Presenter refGenPresenter,
                             final ToolRequestView.Presenter toolReqPresenter,
                             final ToolAdminView.Presenter toolAdminPresenter,
                             final SystemMessageView.Presenter sysMsgPresenter,
                             final TemplateListingView.Presenter tempPresenter,
                             final PermanentIdRequestView.Presenter permIdPresenter,
                             final DEProperties toolIntProps,
                             final BelphegorViewAppearance appearance) {
        this.appearance = appearance;
        this.presenter = presenter;
        this.refGenPresenter = refGenPresenter;
        this.toolReqPresenter = toolReqPresenter;
        this.toolAdminPresenter = toolAdminPresenter;
        this.sysMsgPresenter = sysMsgPresenter;
        this.tempPresenter = tempPresenter;
        this.permIdPresenter = permIdPresenter;

        initWidget(uiBinder.createAndBindUi(this));
        init(presenter,
             refGenPresenter,
             toolReqPresenter,
             toolAdminPresenter,
             sysMsgPresenter,
             tempPresenter,
             permIdPresenter,
             toolIntProps);
        ensureDebugId(Belphegor.Ids.BELPHEGOR);
    }

    @UiFactory
    HtmlLayoutContainer buildHtmlLayoutContainer() {
        return new HtmlLayoutContainer(appearance.renderNorthContainer());
    }

    private void init(final AdminAppsView.AdminPresenter presenter,
                      final RefGenomeView.Presenter refGenPresenter,
                      final ToolRequestView.Presenter toolReqPresenter,
                      final ToolAdminView.Presenter toolAdminPresenter,
                      final SystemMessageView.Presenter sysMsgPresenter,
                      final TemplateListingView.Presenter tempPresenter,
                      final PermanentIdRequestView.Presenter permIdPresenter,
                      final DEProperties toolIntProps) {
        buildUserMenu();
        // Select Beta group by default.
        HasId betaGroup = CommonModelUtils.getInstance().createHasIdFromString(toolIntProps.getDefaultBetaCategoryId());
        presenter.go(appsPanel, betaGroup);
        refGenPresenter.go(refGenomePanel);
        toolReqPresenter.go(toolRequestPanel);
        toolAdminPresenter.go(toolAdminPanel);
        sysMsgPresenter.go(systemMessagesPanel);
        tempPresenter.go(metadataPanel);
        permIdPresenter.go(permIdPanel);
    }

    private void buildUserMenu() {

        Menu userMenu = new Menu();

        userMenu.setBorders(true);
        userMenu.setStyleName(appearance.style().headerMenuBody());
        userMenu.add(new IPlantAnchor(appearance.logout(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doLogout();
            }
        }));

        String username = UserInfo.getInstance().getUsername();
        String firstName = UserInfo.getInstance().getFirstName();
        String lastName = UserInfo.getInstance().getLastName();
        String menuLabel = (firstName != null && lastName != null) ? firstName + " " + lastName : username;
        menuButton = new TextButton(menuLabel);
        menuButton.setMenu(userMenu);

        northCon.add(menuButton, new HtmlData("." + appearance.style().headerMenu()));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        menuButton.ensureDebugId(baseID + Belphegor.Ids.MENU_BUTTON);

        appsPanel.ensureDebugId(baseID + Belphegor.Ids.APPS);
        presenter.setViewDebugId(baseID + Belphegor.Ids.APPS);

        refGenomePanel.ensureDebugId(baseID + Belphegor.Ids.REFERENCE_GENOME);
        refGenPresenter.setViewDebugId(baseID + Belphegor.Ids.REFERENCE_GENOME);

        toolRequestPanel.ensureDebugId(baseID + Belphegor.Ids.TOOL_REQUEST);
        toolReqPresenter.setViewDebugId(baseID + Belphegor.Ids.TOOL_REQUEST);

        toolAdminPanel.ensureDebugId(baseID + Belphegor.Ids.TOOL_ADMIN);
        toolAdminPresenter.setViewDebugId(baseID + Belphegor.Ids.TOOL_ADMIN);

        systemMessagesPanel.ensureDebugId(baseID + Belphegor.Ids.SYSTEM_MESSAGE);
        sysMsgPresenter.setViewDebugId(baseID + Belphegor.Ids.SYSTEM_MESSAGE);

        metadataPanel.ensureDebugId(baseID + Belphegor.Ids.METADATA);
        tempPresenter.setViewDebugId(baseID + Belphegor.Ids.METADATA);

        permIdPanel.ensureDebugId(baseID + Belphegor.Ids.PERMID);
        permIdPresenter.setViewDebugId(baseID + Belphegor.Ids.PERMID);
    }

    @Override
    public void doLogout() {
        Window.Location.assign(appearance.logoutWindowUrl());
    }
}

package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.views.AppsView.Presenter;
import org.iplantc.de.apps.client.views.widgets.AppFavoriteCellWidget;
import org.iplantc.de.apps.client.views.widgets.AppRatingCellWidget;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppInfoView implements IsWidget {

    interface AppInfoViewUiBinder extends UiBinder<Widget, AppInfoView> {
    }

    public interface AppDetailsRenderer extends XTemplates {
        @XTemplate(source = "appDetails.html")
        public SafeHtml render();
    }

    private static AppInfoViewUiBinder BINDER = GWT.create(AppInfoViewUiBinder.class);

    @UiField
    AppFavoriteCellWidget favIcon;

    @UiField
    HTML appDesc;

    @UiField
    HorizontalPanel appDetailsPnl;

    @UiField
    VerticalLayoutContainer info_container;

    private final TabPanel tabs;

    private ContentPanel dcPanel;

    private AccordionLayoutAppearance appearance;

    private AccordionLayoutContainer dcCon;

    private HtmlLayoutContainer appDetailsHtmlContainer;

    private final App app;

    private final Presenter presenter;

    public AppInfoView(final App app, final Presenter presenter) {
		this.app = app;
        this.presenter = presenter;

        BINDER.createAndBindUi(this);
        favIcon.setValue(this.app);
        initDetailsPnl();
        initDCPanel();
        loadDCinfo();
        tabs = new TabPanel();
    }

    private void addInfoTabs() {
        tabs.add(info_container, I18N.DISPLAY.information());
        tabs.add(dcPanel, I18N.DISPLAY.toolTab());
    }

    private void initDetailsPnl() {
        String description = presenter.highlightSearchText(app.getDescription());

        appDesc.setHTML("<i>" + I18N.DISPLAY.description() + ": " + "</i>" + description);
        AppDetailsRenderer templates = GWT.create(AppDetailsRenderer.class);
        appDetailsHtmlContainer = new HtmlLayoutContainer(templates.render());
        addPubDate(app, appDetailsHtmlContainer);
        addIntegratorsInfo(app, appDetailsHtmlContainer);
        addDocLinks(app, appDetailsHtmlContainer);
        addRating(app, appDetailsHtmlContainer);
        appDetailsPnl.add(appDetailsHtmlContainer);
    }

    private void initDCPanel() {
        dcPanel = new ContentPanel();
        dcPanel.setHeaderVisible(false);
        dcCon = new AccordionLayoutContainer();
        dcCon.setExpandMode(ExpandMode.SINGLE_FILL);
        dcPanel.add(dcCon);
        appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
    }

    private ContentPanel buildDCPanel(DeployedComponent dc) {
        ContentPanel cp = new ContentPanel(appearance);
        cp.setAnimCollapse(false);
        cp.setHeadingText(dc.getName());
        dcCon.add(cp);
        AppDetailsRenderer templates = GWT.create(AppDetailsRenderer.class);
        HtmlLayoutContainer c = new HtmlLayoutContainer(templates.render());
        cp.setWidget(c);
        addDCDetails(dc, c);
        dcCon.setActiveWidget(cp);
        return cp;
    }

    private void addDCDetails(DeployedComponent dc, HtmlLayoutContainer hlc) {
        String name = presenter.highlightSearchText(dc.getName());

        hlc.add(new Label(I18N.DISPLAY.name() + ": "), new HtmlData(".cell1"));
        hlc.add(new HTML(name), new HtmlData(".cell2"));
        hlc.add(new Label(I18N.DISPLAY.description() + ": "), new HtmlData(".cell3"));
        hlc.add(new Label(dc.getDescription()), new HtmlData(".cell4"));
        hlc.add(new Label(I18N.DISPLAY.path() + ": "), new HtmlData(".cell5"));
        hlc.add(new Label(dc.getLocation()), new HtmlData(".cell6"));
        hlc.add(new Label(I18N.DISPLAY.toolVersion() + ": "), new HtmlData(".cell7"));
        hlc.add(new Label(dc.getVersion()), new HtmlData(".cell8"));
        hlc.add(new Label(I18N.DISPLAY.attribution() + ": "), new HtmlData(".cell9"));
        hlc.add(new Label(dc.getAttribution()), new HtmlData(".cell10"));
    }

    private void loadDCinfo() {
        ServicesInjector.INSTANCE.getAppUserServiceFacade().getAppDetails(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                App appDetails = AutoBeanCodex.decode(factory, App.class, result).as();
                List<DeployedComponent> deployedComponents = appDetails.getDeployedComponents();
                if (deployedComponents != null) {
                    for (DeployedComponent component : deployedComponents) {
                        buildDCPanel(component);
                    }
                }

                addGroups(appDetails.getGroups());

                addInfoTabs();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.deployedComponentRetrievalFailure(), caught);
            }
        });
    }

    private void addIntegratorsInfo(final App app, HtmlLayoutContainer hlc) {
        String name = presenter.highlightSearchText(app.getIntegratorName());

        hlc.add(new Label(I18N.DISPLAY.integratorName() + ": "), new HtmlData(".cell3"));
        hlc.add(new HTML(name), new HtmlData(".cell4"));
        hlc.add(new Label(I18N.DISPLAY.integratorEmail() + ": "), new HtmlData(".cell5"));
        hlc.add(new Label(app.getIntegratorEmail()), new HtmlData(".cell6"));
    }

    private void addDocLinks(final App app, HtmlLayoutContainer hlc) {
        hlc.add(new Label(I18N.DISPLAY.help() + ": "), new HtmlData(".cell7"));
        IPlantAnchor doc = new IPlantAnchor(I18N.DISPLAY.documentation(), 100, new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                Window.open(app.getWikiUrl(), "_blank", "");

            }
        });

        hlc.add(doc, new HtmlData(".cell8"));
    }

    private void addRating(final App app, HtmlLayoutContainer hlc) {
        hlc.add(new Label(I18N.DISPLAY.rating() + ": "), new HtmlData(".cell9"));
        AppRatingCellWidget rcell = new AppRatingCellWidget();
        rcell.setValue(app);
        hlc.add(rcell, new HtmlData(".cell10"));
    }

    private void addGroups(List<AppGroup> groups) {
        if (groups == null) {
            return;
        }

        List<String> groupNames = Lists.newArrayList();
        for (AppGroup group : groups) {
            groupNames.add(group.getName());
        }
        Collections.sort(groupNames);

        appDetailsHtmlContainer.add(new Label(I18N.DISPLAY.category() + ": "), new HtmlData(".cell11"));
        appDetailsHtmlContainer.add(new HTML(Joiner.on(", ").join(groupNames)), new HtmlData(".cell12"));
    }

    private void addPubDate(final App app, HtmlLayoutContainer hlc) {
        hlc.add(new Label(I18N.DISPLAY.publishedOn() + ": "), new HtmlData(".cell1"));
        Date pub_date = app.getIntegrationDate();
        if (pub_date != null) {
            hlc.add(new Label(pub_date.toString()), new HtmlData(".cell2"));
        } else {
            hlc.add(new Label("-"), new HtmlData(".cell2"));
        }
    }

    @Override
    public Widget asWidget() {
        return tabs;
    }

}


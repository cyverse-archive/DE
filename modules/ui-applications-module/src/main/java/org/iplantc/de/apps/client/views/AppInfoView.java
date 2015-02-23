package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.apps.client.views.widgets.AppFavoriteCellWidget;
import org.iplantc.de.apps.client.views.widgets.AppRatingCellWidget;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppInfoView implements
                        IsWidget,
                        AppFavoriteCell.RequestAppFavoriteEventHandler,
                        AppFavoriteCell.HasRequestAppFavoriteEventHandlers,
                        AppFavoritedEvent.AppFavoritedEventHandler {

    private static final String DIV_CLOSE = "</div>";

    private static final String MARKDOWN_DIV_OPEN = "<link href=\"./markdown.css\" rel=\"stylesheet\"></link><div style='background-color:white;overflow:scroll;height:450px;' class=\"markdown\">";

    private final class DisplayAppDocCallback implements AsyncCallback<String> {
        private final App app;
        private HtmlLayoutContainer docWidget;
        private AppDocEditView editView;

        private DisplayAppDocCallback(App app) {
            this.app = app;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post("Unable to retrieve documentation for this app!", caught);
        }

        @Override
        public void onSuccess(String result) {
            AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
            AppDoc doc = AutoBeanCodex.decode(factory, AppDoc.class, result).as();
            String docString = doc.getDocumentaion();
            List<String> refLinks = doc.getReferences();
            showDoc(docString, refLinks);
        }

        void showDoc(String docString, List<String> refLinks) {
            // FIXME Roll into appearance
            String email = app.getIntegratorEmail();
            PlainTabPanel tabs = new PlainTabPanel();
            String renderHtml = render(docString);
            final String ref_link_html = buildRefLinkHtml(refLinks);
            docWidget = new HtmlLayoutContainer("");
            if (!Strings.isNullOrEmpty(ref_link_html)) {
                setDocHtml(renderHtml + ref_link_html);
            } else {
                setDocHtml(renderHtml);
            }

            if (UserInfo.getInstance().getEmail().equals(email)) {
                editView = new AppDocEditView(app.getId(), docString, appUserService, new Command() {

                    @Override
                    public void execute() {
                        docWidget.clear();
                        if (!Strings.isNullOrEmpty(ref_link_html)) {
                            setDocHtml(editView.getDoc() + ref_link_html);
                        } else {
                            setDocHtml(editView.getDoc());
                        }
                    }
                });
            }

            com.sencha.gxt.widget.core.client.Window d = new com.sencha.gxt.widget.core.client.Window();
            d.setHeadingHtml(app.getName());
            if (editView != null) {
                tabs.add(editView.asWidget(), I18N.DISPLAY.edit());
            }
            tabs.add(docWidget, I18N.DISPLAY.documentation());
            d.add(tabs);
            d.setSize("700px", "500px");
            d.setResizable(false);
            d.show();
        }

        private String buildRefLinkHtml(List<String> refLinks) {
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            builder.appendHtmlConstant("<h4>" + I18N.DISPLAY.refLbl() + "</h4><ul>");
            if (refLinks != null & refLinks.size() > 0) {
                for (String ref : refLinks) {
                    builder.appendHtmlConstant("<li>" + ref + "</li>");
                }
                builder.appendHtmlConstant("</ul>");
                return builder.toSafeHtml().asString();
            } else {
                return null;
            }

        }

        private void setDocHtml(String renderHtml) {
            docWidget.setHTML(MARKDOWN_DIV_OPEN + renderHtml + DIV_CLOSE);

        }
    }

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

    private final AppsView appsView;
    private final AppUserServiceFacade appUserService;

    public AppInfoView(final App app, final AppsView appsView, final AppUserServiceFacade appUserService) {
        this.app = app;
        this.appsView = appsView;
        this.appUserService = appUserService;

        BINDER.createAndBindUi(this);
        favIcon.setValue(this.app);
        favIcon.addRequestAppFavoriteEventHandlers(this);
        initDetailsPnl();
        initDCPanel();
        loadDCinfo();
        tabs = new TabPanel();
        info_container.setScrollMode(ScrollMode.AUTO);
    }

    @Override
    public HandlerRegistration
            addRequestAppFavoriteEventHandlers(AppFavoriteCell.RequestAppFavoriteEventHandler handler) {
        return asWidget().addHandler(handler, AppFavoriteCell.REQUEST_APP_FAV_EVNT_TYPE);
    }

    @Override
    public void onAppFavoriteRequest(AppFavoriteCell.RequestAppFavoriteEvent event) {
        // Forward event
        asWidget().fireEvent(event);
    }

    @Override
    public void onAppFavorited(AppFavoritedEvent appFavoritedEvent) {
        favIcon.setValue(appFavoritedEvent.getApp());
    }

    private void addInfoTabs() {
        tabs.add(info_container, I18N.DISPLAY.information());
        tabs.add(dcPanel, I18N.DISPLAY.toolTab());
    }

    private void initDetailsPnl() {
        String description = appsView.highlightSearchText(app.getDescription());

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

    private ContentPanel buildDCPanel(Tool dc) {
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

    private void addDCDetails(Tool dc, HtmlLayoutContainer hlc) {
        String name = appsView.highlightSearchText(dc.getName());

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
        appUserService.getAppDetails(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                App appDetails = AutoBeanCodex.decode(factory, App.class, result).as();
                List<Tool> deployedComponents = appDetails.getTools();
                if (deployedComponents != null) {
                    for (Tool component : deployedComponents) {
                        buildDCPanel(component);
                    }
                }

                // gets immediate parent only
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
        String name = appsView.highlightSearchText(app.getIntegratorName());

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
                if (!Strings.isNullOrEmpty(app.getWikiUrl())) {
                    Window.open(app.getWikiUrl(), "_blank", "");
                } else {
                    appUserService.getAppDoc(app.getId(), new DisplayAppDocCallback(app));
                }

            }
        });

        hlc.add(doc, new HtmlData(".cell8"));
    }

    public static native String render(String val) /*-{
		var markdown = $wnd.Markdown.getSanitizingConverter();
		return markdown.makeHtml(val);
    }-*/;

    private void addRating(final App app, HtmlLayoutContainer hlc) {
        hlc.add(new Label(I18N.DISPLAY.rating() + ": "), new HtmlData(".cell9"));
        AppRatingCellWidget rcell = new AppRatingCellWidget();
        rcell.setValue(app);
        hlc.add(rcell, new HtmlData(".cell10"));
    }

    private void addGroups(List<AppCategory> groups) {
        if (groups == null || groups.size() <= 0) {
            return;
        }

        appDetailsHtmlContainer.add(new Label(I18N.DISPLAY.category() + ": "), new HtmlData(".cell11"));
        List<String> builder = new ArrayList<String>();
        for (AppCategory ag : groups) {
            builder.add(Joiner.on(" >> ").join(appsView.computeGroupHierarchy(ag)));
        }
        Collections.sort(builder);
        appDetailsHtmlContainer.add(new HTML(Joiner.on("<br/>").join(builder)), new HtmlData(".cell12"));

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

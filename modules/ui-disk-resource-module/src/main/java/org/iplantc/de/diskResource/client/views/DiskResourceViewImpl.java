package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

import java.util.logging.Logger;

/**
 * FIXME Factor out appearance. This class is not testable in it's current form.
 * FIXME Factor out details panel.
 *
 * @author jstroot, sriram, psarando
 */
public class DiskResourceViewImpl extends Composite implements DiskResourceView {

    @UiTemplate("DiskResourceView.ui.xml")
    interface DiskResourceViewUiBinder extends UiBinder<Widget, DiskResourceViewImpl> {
    }

    private static DiskResourceViewUiBinder BINDER = GWT.create(DiskResourceViewUiBinder.class);


    @UiField BorderLayoutContainer con;
    @UiField BorderLayoutData westData;
    @UiField BorderLayoutData centerData;
    @UiField BorderLayoutData eastData;
    @UiField BorderLayoutData northData;
    @UiField BorderLayoutData southData;

    @UiField(provided = true) NavigationView navigationView;
    @UiField(provided = true) GridView centerGridView;
    @UiField(provided = true) ToolbarView toolbar;
    @UiField(provided = true) DetailsView detailsView;


    Logger LOG = Logger.getLogger("DRV");

    @Inject
    DiskResourceViewImpl(final IplantDisplayStrings displayStrings,
                         final DiskResourceUtil diskResourceUtil,
                         final TagListPresenterFactory tagListPresenterFactory,
                         @Assisted final DiskResourceView.Presenter presenter,
                         @Assisted final NavigationView.Presenter navigationPresenter,
                         @Assisted final GridView.Presenter gridViewPresenter,
                         @Assisted final ToolbarView.Presenter toolbarPresenter,
                         @Assisted final DetailsView.Presenter detailsPresenter) {
        this.navigationView = navigationPresenter.getView();
        this.centerGridView = gridViewPresenter.getView();
        this.toolbar = toolbarPresenter.getView();
        this.detailsView = detailsPresenter.getView();
        // FIXME Wire up details view

        initWidget(BINDER.createAndBindUi(this));

//        detailsPanel.setScrollMode(ScrollMode.AUTO);

        // by default no details to show...
//        resetDetailsPanel();

        con.setNorthWidget(toolbar, northData);

    }

//    @Override
//    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
//        FIXME Move to Details View
//        if (event.getSelection().isEmpty()) {
//            resetDetailsPanel();
//        }
//    }

    @UiFactory
    public ValueProvider<Folder, String> createValueProvider() {
        return new ValueProvider<Folder, String>() {

            @Override
            public String getValue(Folder object) {
                return object.getName();
            }

            @Override
            public void setValue(Folder object, String value) {}

            @Override
            public String getPath() {
                return "name"; //$NON-NLS-1$
            }
        };
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolbar.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.MENU_BAR);
    }

    @Override
    public void setEastWidgetHidden(boolean hideEastWidget) {
        eastData.setHidden(hideEastWidget);
    }

    @Override
    public void setNorthWidgetHidden(boolean hideNorthWidget) {
        northData.setHidden(hideNorthWidget);
    }

    @Override
    public void setSouthWidget(IsWidget widget) {
        southData.setHidden(false);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public void setSouthWidget(IsWidget widget, double size) {
        southData.setHidden(false);
        southData.setSize(size);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);
    }

    @Override
    public void unmask() {
        con.unmask();
    }
}

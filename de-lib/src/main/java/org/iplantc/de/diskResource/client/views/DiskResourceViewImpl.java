package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.share.DiskResourceModule;

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

/**
 * @author jstroot, sriram, psarando
 */
public class DiskResourceViewImpl extends Composite implements DiskResourceView {

    @UiTemplate("DiskResourceView.ui.xml")
    interface DiskResourceViewUiBinder extends UiBinder<Widget, DiskResourceViewImpl> {
    }

    private static final DiskResourceViewUiBinder BINDER = GWT.create(DiskResourceViewUiBinder.class);

    @UiField BorderLayoutContainer con;
    @UiField BorderLayoutData westData;
    @UiField BorderLayoutData centerData;
    @UiField BorderLayoutData eastData;
    @UiField BorderLayoutData northData;
    @UiField BorderLayoutData southData;

    @UiField(provided = true) final NavigationView navigationView;
    @UiField(provided = true) final GridView centerGridView;
    @UiField(provided = true) final ToolbarView toolbar;
    @UiField(provided = true) final DetailsView detailsView;

    @Inject
    DiskResourceViewImpl(@Assisted final NavigationView.Presenter navigationPresenter,
                         @Assisted final GridView.Presenter gridViewPresenter,
                         @Assisted final ToolbarView.Presenter toolbarPresenter,
                         @Assisted final DetailsView.Presenter detailsPresenter) {
        this.navigationView = navigationPresenter.getView();
        this.centerGridView = gridViewPresenter.getView();
        this.toolbar = toolbarPresenter.getView();
        this.detailsView = detailsPresenter.getView();

        initWidget(BINDER.createAndBindUi(this));

        // FIXME This shouldn't be necessary
        con.setNorthWidget(toolbar, northData);
    }

    // FIXME This shouldn't be necessary
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
        centerGridView.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.GRID);
        navigationView.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.NAVIGATION);
        detailsView.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.DETAILS);
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

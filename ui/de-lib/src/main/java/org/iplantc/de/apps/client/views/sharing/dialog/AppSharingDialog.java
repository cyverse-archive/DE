/**
 * 
 * @author sriram
 */

package org.iplantc.de.apps.client.views.sharing.dialog;

import org.iplantc.de.apps.client.presenter.sharing.AppSharingPresenter;
import org.iplantc.de.apps.client.views.sharing.AppSharingView;
import org.iplantc.de.apps.client.views.sharing.AppSharingViewImpl;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

public class AppSharingDialog extends IPlantDialog implements SelectHandler {

    private final AppUserServiceFacade appService;
    private SharingPresenter sharingPresenter;

    @Inject
    CollaboratorsUtil collaboratorsUtil;
    @Inject
    JsonUtil jsonUtil;

    @Inject
    AppSharingDialog(final AppUserServiceFacade appService) {
        super(true);
        this.appService = appService;
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        // addHelp(new HTML(appearance.sharePermissionsHelp()));
        setHeadingText("Manage Sharing");
        setOkButtonText("Done");
        addOkButtonSelectHandler(this);

    }

    @Override
    public void onSelect(SelectEvent event) {
        Preconditions.checkNotNull(sharingPresenter);
        sharingPresenter.processRequest();
    }

    public void show(final List<App> resourcesToShare) {
        ListStore<App> appStore = new ListStore<>(new ModelKeyProvider<App>() {

            @Override
            public String getKey(App item) {
                return item.getId();
            }
        });
        AppSharingView view = new AppSharingViewImpl(buildAppColumnModel(), appStore);
        view.setSelectedApps(resourcesToShare);
        sharingPresenter = new AppSharingPresenter(appService,
                                                   resourcesToShare,
                                                   view,
                                                   collaboratorsUtil,
                                                   jsonUtil);
        sharingPresenter.go(this);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. ");
    }

    private ColumnModel<App> buildAppColumnModel() {
        List<ColumnConfig<App, ?>> list = new ArrayList<>();

        ColumnConfig<App, String> name = new ColumnConfig<>(new ValueProvider<App, String>() {

            @Override
            public String getValue(App object) {
                return object.getName();
            }

            @Override
            public void setValue(App object, String value) {
                // TODO Auto-generated method stub
            }

            @Override
            public String getPath() {
                return "name";
            }
        }, 180, "Name");
        list.add(name);
        return new ColumnModel<>(list);
    }

}

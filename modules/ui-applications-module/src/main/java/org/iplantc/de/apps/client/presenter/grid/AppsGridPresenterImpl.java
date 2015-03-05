package org.iplantc.de.apps.client.presenter.grid;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.models.AppModelKeyProvider;
import org.iplantc.de.client.models.apps.App;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public class AppsGridPresenterImpl implements AppsGridView.Presenter {

    private final ListStore<App> listStore;
    private final AppsGridView view;

    public AppsGridPresenterImpl() {
        this.listStore = new ListStore<>(new AppModelKeyProvider());
        this.view = null;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        // Tell view to update header
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {

        // Tell view to update header
    }
}

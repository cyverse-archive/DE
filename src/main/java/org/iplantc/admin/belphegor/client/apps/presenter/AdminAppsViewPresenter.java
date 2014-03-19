package org.iplantc.admin.belphegor.client.apps.presenter;

import org.iplantc.de.apps.client.views.AppsView.Presenter;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

public interface AdminAppsViewPresenter extends Presenter {

    boolean canMoveAppGroup(AppGroup parentGroup, AppGroup childGroup);

    boolean canMoveApp(final AppGroup parentGroup, final App app);

    void moveAppGroup(final AppGroup parentGroup, final AppGroup childGroup);

    void moveApp(final AppGroup parentGroup, final App app);
}

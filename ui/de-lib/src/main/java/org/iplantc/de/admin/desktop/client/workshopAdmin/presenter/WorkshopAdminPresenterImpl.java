package org.iplantc.de.admin.desktop.client.workshopAdmin.presenter;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.data.shared.ListStore;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.admin.desktop.client.workshopAdmin.gin.factory.WorkshopAdminViewFactory;
import org.iplantc.de.admin.desktop.client.workshopAdmin.model.MemberProperties;
import org.iplantc.de.admin.desktop.client.workshopAdmin.service.WorkshopAdminServiceFacade;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.Member;

/**
 * @author dennis
 */
public class WorkshopAdminPresenterImpl implements WorkshopAdminView.Presenter {

    private final WorkshopAdminView view;
    private final WorkshopAdminServiceFacade serviceFacade;
    private final GroupAutoBeanFactory groupAutoBeanFactory;
    private final WorkshopAdminView.WorkshopAdminViewAppearance appearance;
    private final ListStore<Member> listStore;

    @Inject
    public WorkshopAdminPresenterImpl(final WorkshopAdminViewFactory viewFactory,
                                      final WorkshopAdminServiceFacade serviceFacade,
                                      final GroupAutoBeanFactory groupAutoBeanFactory,
                                      final MemberProperties memberProperties,
                                      final WorkshopAdminView.WorkshopAdminViewAppearance appearance) {
        listStore = new ListStore<>(memberProperties.id());
        view = viewFactory.create(listStore);
        this.serviceFacade = serviceFacade;
        this.groupAutoBeanFactory = groupAutoBeanFactory;
        this.appearance = appearance;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.WorkshopAdminIds.VIEW);
    }
}

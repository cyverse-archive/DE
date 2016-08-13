package org.iplantc.de.admin.desktop.client.workshopAdmin.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.grid.Grid;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.client.models.groups.Member;

/**
 * @author dennis
 */
public class WorkshopAdminViewImpl extends Composite implements WorkshopAdminView {

    interface WorkshopAdminViewImplUiBinder extends UiBinder<Widget, WorkshopAdminViewImpl> {}

    @UiField TextButton addButton;
    @UiField TextButton deleteButton;
    @UiField Grid<Member> grid;
    @UiField(provided = true) WorkshopAdminViewAppearance appearance;

    private static WorkshopAdminViewImplUiBinder uiBinder = GWT.create(WorkshopAdminViewImplUiBinder.class);
}

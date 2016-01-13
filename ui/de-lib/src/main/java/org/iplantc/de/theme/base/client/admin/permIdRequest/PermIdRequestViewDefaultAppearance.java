package org.iplantc.de.theme.base.client.admin.permIdRequest;

import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestView.PermIdRequestViewAppearance;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class PermIdRequestViewDefaultAppearance implements PermIdRequestViewAppearance {

    private final PermIdRequestDisplayStrings displayStrings;
    private final IplantResources iplantResources;
    private final IplantDisplayStrings iplantDisplayStrings;

    public PermIdRequestViewDefaultAppearance() {
        this(GWT.<PermIdRequestDisplayStrings> create(PermIdRequestDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    public PermIdRequestViewDefaultAppearance(PermIdRequestDisplayStrings displayStrings,
                                              IplantResources iplantResources,
                                              IplantDisplayStrings iplantDisplayStrings) {
        this.displayStrings = displayStrings;
        this.iplantResources = iplantResources;
        this.iplantDisplayStrings = iplantDisplayStrings;

    }

    @Override
    public String dateSubmittedColumnLabel() {
        return displayStrings.dateSubmittedColumnLabel();
    }

    @Override
    public int dateSubmittedColumnWidth() {
        return 90;
    }

    @Override
    public String dateUpdatedColumnLabel() {
        return displayStrings.dateUpdatedColumnLabel();
    }

    @Override
    public int dateUpdatedColumnWidth() {
        return 90;
    }

    @Override
    public String nameColumnLabel() {
        return displayStrings.nameColumnLabel();
    }

    @Override
    public int nameColumnWidth() {
        return 90;
    }

    @Override
    public String submitBtnText() {
        return displayStrings.submitBtnText();
    }

    @Override
    public String updateBtnText() {
        return displayStrings.updateBtnText();
    }

    @Override
    public ImageResource updateIcon() {
        return iplantResources.add();
    }

    @Override
    public String pathColumnLabel() {
        return displayStrings.pathColumnLabel();
    }

    @Override
    public int pathColumnWidth() {
        return 90;
    }

    @Override
    public int northPanelSize() {
        return 30;
    }

    @Override
    public int eastPanelSize() {
        return 0;
    }

    @Override
    public String noRows() {
        return displayStrings.noRows();
    }

    @Override
    public String confirmCreate(String type) {
        return displayStrings.confirmCreate(type);
    }

    @Override
    public String createBtnText() {
        return displayStrings.createBtnText();
    }

    @Override
    public String currentStatusLbl() {
        return displayStrings.currentStatusLbl();
    }

    @Override
    public String setStatusLbl() {
        return displayStrings.setStatusLbl();
    }

    @Override
    public String commentsLbl() {
        return displayStrings.commentsLbl();
    }

    @Override
    public String updateStatus() {
        return displayStrings.updateStatus();
    }

    @Override
    public String update() {
        return displayStrings.update();
    }

    @Override
    public String request() {
        return displayStrings.request();
    }

}

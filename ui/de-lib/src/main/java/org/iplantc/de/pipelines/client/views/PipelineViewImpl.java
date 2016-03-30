package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.client.models.pipelines.PipelineTask;
import org.iplantc.de.pipelineBuilder.client.builder.PipelineCreator;
import org.iplantc.de.pipelines.shared.Pipelines;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

import java.util.ArrayList;
import java.util.List;

/**
 * The main PipelineView implementation.
 *
 * @author psarando
 *
 */
public class PipelineViewImpl extends Composite implements PipelineView {

    private static PipelineViewUiBinder uiBinder = GWT.create(PipelineViewUiBinder.class);
    private final Driver driver = GWT.create(Driver.class);
    private Presenter presenter;

    @UiTemplate("PipelineView.ui.xml")
    interface PipelineViewUiBinder extends UiBinder<Widget, PipelineViewImpl> {
    }

    interface Driver extends SimpleBeanEditorDriver<Pipeline, PipelineViewImpl> {
    }

    public PipelineViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        driver.initialize(this);

        ToggleGroup group = new ToggleGroup();
        group.add(infoBtn);
        group.add(appOrderBtn);
        group.add(mappingBtn);
    }

    @UiField
    BorderLayoutContainer borders;

    @UiField
    BorderLayoutData northData;

    @UiField
    CardLayoutContainer centerPanel;

    @UiField
    BorderLayoutContainer builderPanel;

    @UiField
    SimpleContainer builderDropWrapper;

    @UiField
    PipelineCreator builder;

    @UiField
    SimpleContainer appsContainer;

    @UiField
    BorderLayoutContainer stepEditorPanel;

    @UiField
    CardLayoutContainer stepPanel;

    @UiField
    @Path("")
    PipelineInfoEditor infoPanel;

    @UiField
    @Path("")
    PipelineAppOrderViewImpl appOrderPanel;

    @UiField
    @Path("apps")
    PipelineAppMappingForm mappingPanel;

    @UiField
    @Editor.Ignore
    ToggleButton infoBtn;

    @UiField
    @Editor.Ignore
    ToggleButton appOrderBtn;

    @UiField
    @Editor.Ignore
    ToggleButton mappingBtn;

    @UiField
    HtmlLayoutContainer helpContainer;

    @UiHandler("infoBtn")
    public void onInfoClick(SelectEvent e) {
        presenter.onInfoClick();
    }

    @UiHandler("appOrderBtn")
    public void onAppOrderClick(SelectEvent e) {
        presenter.onAppOrderClick();
    }

    @UiHandler("mappingBtn")
    public void onMappingClick(SelectEvent e) {
        presenter.onMappingClick();
    }

    @UiFactory
    public HtmlLayoutContainer buildHelpContainer() {
        return new HtmlLayoutContainer(I18N.DISPLAY.infoPnlTip());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPipeline(Pipeline pipeline) {
        if (pipeline.getApps() == null) {
            pipeline.setApps(new ArrayList<PipelineTask>());
        }

        driver.edit(pipeline);
    }

    @Override
    public boolean isValid() {
        return driver.flush() != null && !driver.hasErrors();
    }

    @Override
    public List<EditorError> getErrors() {
        if (driver.hasErrors()) {
            return driver.getErrors();
        }

        return null;
    }

    @Override
    public void clearInvalid() {
        clearInvalid(infoBtn);
        clearInvalid(appOrderBtn);
        clearInvalid(mappingBtn);

        infoPanel.clearInvalid();
        mappingPanel.clearInvalid();
    }

    private void clearInvalid(ToggleButton btn) {
        btn.setIcon(null);
        clearErrorTip(btn);
    }

    private void clearErrorTip(ToggleButton btn) {
        ToolTip toolTip = btn.getToolTip();
        if (toolTip != null) {
            toolTip.disable();
        }
    }

    @Override
    public Pipeline getPipeline() {
        return driver.flush();
    }

    @Override
    public void setNorthWidget(IsWidget widget) {
        borders.setNorthWidget(widget, northData);
    }

    @Override
    public IsWidget getActiveView() {
        return centerPanel.getActiveWidget();
    }

    @Override
    public void setActiveView(IsWidget view) {
        centerPanel.setActiveWidget(view);
    }

    @Override
    public BorderLayoutContainer getBuilderPanel() {
        return builderPanel;
    }

    @Override
    public SimpleContainer getBuilderDropContainer() {
        return builderDropWrapper;
    }

    @Override
    public PipelineCreator getPipelineCreator() {
        return builder;
    }

    @Override
    public SimpleContainer getAppsContainer() {
        return appsContainer;
    }

    @Override
    public BorderLayoutContainer getStepEditorPanel() {
        return stepEditorPanel;
    }

    @Override
    public CardLayoutContainer getStepPanel() {
        return stepPanel;
    }

    @Override
    public IsWidget getInfoPanel() {
        return infoPanel;
    }

    @Override
    public PipelineAppOrderView getAppOrderPanel() {
        return appOrderPanel;
    }

    @Override
    @Editor.Ignore
    public PipelineAppMappingView getMappingPanel() {
        return mappingPanel;
    }

    @Override
    public ListStore<PipelineTask> getPipelineAppStore() {
        return appOrderPanel.getPipelineAppStore();
    }

    @Override
    public PipelineTask getOrderGridSelectedApp() {
        return appOrderPanel.getOrderGridSelectedApp();
    }

    @Override
    @Editor.Ignore
    public ToggleButton getInfoBtn() {
        return infoBtn;
    }

    @Override
    @Editor.Ignore
    public ToggleButton getAppOrderBtn() {
        return appOrderBtn;
    }

    @Override
    @Editor.Ignore
    public ToggleButton getMappingBtn() {
        return mappingBtn;
    }

    @Override
    public HtmlLayoutContainer getHelpContainer() {
        return helpContainer;
    }

    @Override
    public void markInfoBtnValid() {
        markValid(infoBtn);
    }

    @Override
    public void markInfoBtnInvalid(String error) {
        markInvalid(infoBtn, error);
    }

    @Override
    public void markAppOrderBtnValid() {
        markValid(appOrderBtn);
    }

    @Override
    public void markAppOrderBtnInvalid(String error) {
        markInvalid(appOrderBtn, error);
    }

    @Override
    public void markMappingBtnValid() {
        markValid(mappingBtn);
    }

    @Override
    public void markMappingBtnInvalid(String error) {
        markInvalid(mappingBtn, error);
    }

    private void markValid(ToggleButton btn) {
        btn.setIcon(IplantResources.RESOURCES.tick());
        clearErrorTip(btn);
    }

    private void markInvalid(ToggleButton btn, String error) {
        btn.setIcon(IplantResources.RESOURCES.exclamation());
        btn.setToolTip(error);
        btn.getToolTip().enable();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        appOrderPanel.asWidget().ensureDebugId(baseID + Pipelines.Ids.APP_ORDER);
    }
}

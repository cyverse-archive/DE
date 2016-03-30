package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.errorHandling.ServiceErrorCode;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.HasInvalidHandlers;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Abstract class for single select DiskResource fields.
 *
 * @author jstroot
 */
public abstract class AbstractDiskResourceSelector<R extends DiskResource> extends Component implements
                                                                                             IsField<R>,
                                                                                             ValueAwareEditor<R>,
                                                                                             HasValueChangeHandlers<R>,
                                                                                             HasEditorErrors<R>,
                                                                                             DndDragEnterHandler,
                                                                                             DndDragMoveHandler,
                                                                                             DndDropHandler,
                                                                                             HasInvalidHandlers,
                                                                                             DiskResourceSelector,
                                                                                             DiskResourceSelector.HasDisableBrowseButtons {


    public interface SelectorAppearance {

        String analysisFailureWarning(String s);

        ImageResource arrowUndo();

        String browse();

        String cell1();

        String cell2();

        String cell3();

        String diskResourceDoesNotExist(String diskResourcePath);

        String errorTextStyle();

        IPlantSideErrorHandler getSideErrorHandler(TextField input,
                                                   Widget container,
                                                   Widget button, Widget resetBtn);

        String nonDefaultFolderWarning();

        void onResize(int width, int offsetWidth1, int offsetWidth2, Component input,
                      boolean errorHandlerShowing);

        String permissionSelectErrorMessage();

        SafeHtml renderTable();

        SafeHtml dataDragDropStatusText(int size);
    }

    private final SelectorAppearance appearance;

    private final TextButton button;
    private final TextButton resetBtn;

    private final DiskResourceServiceFacade drServiceFacade;
    private final List<EditorError> errors = Lists.newArrayList();
    private final Element infoText;
    private final TextField input = new TextField();
    private final IplantValidationConstants vConstants;
    private boolean browseButtonEnabled = true;
    private final IPlantSideErrorHandler errorHandler;
    private DefaultEditorError existsEditorError = null;
    private String infoTextString;
    private R model;
    private DefaultEditorError permissionEditorError = null;
    // by default do not validate permissions
    private boolean validatePermissions = false;
    private final DiskResourceUtil diskResourceUtil;


    protected AbstractDiskResourceSelector(final DiskResourceServiceFacade diskResourceService,
                                           final SelectorAppearance appearance) {
        this.appearance = appearance;
        drServiceFacade = diskResourceService;
        diskResourceUtil = DiskResourceUtil.getInstance();

        SimplePanel panel = new SimplePanel();
        setElement(panel.getElement());


        HtmlLayoutContainer c = new HtmlLayoutContainer(appearance.renderTable());
        panel.setWidget(c);


        resetBtn = new TextButton("", appearance.arrowUndo());
        c.add(resetBtn, new HtmlData(appearance.cell1()));
        resetBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                clear();
            }
        });

        input.setReadOnly(true);
        c.add(input, new HtmlData(appearance.cell2()));


        button = new TextButton(appearance.browse());
        c.add(button, new HtmlData(appearance.cell3()));
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (!browseButtonEnabled) {
                    return;
                }
                onBrowseSelected();
            }
        });


        infoText = DOM.createDiv();
        infoText.getStyle().setDisplay(Display.NONE);
        getElement().appendChild(infoText);

        initDragAndDrop();

        errorHandler = appearance.getSideErrorHandler(input, this, button,
                                                      resetBtn);

        errorHandler.setAdjustTargetWidth(false);
        input.setErrorSupport(errorHandler);
        sinkEvents(Event.ONCHANGE | Event.ONCLICK | Event.MOUSEEVENTS);

        vConstants = GWT.create(IplantValidationConstants.class);
    }

    @Override
    public HandlerRegistration addInvalidHandler(InvalidHandler handler) {
        return input.addInvalidHandler(handler);
    }

    public void addValidator(Validator<String> validator) {
        if (validator != null) {
            input.addValidator(validator);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<R> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void clear() {
        input.clear();
        model = null;
    }

    @Override
    public void clearInvalid() {
        input.clearInvalid();
        errors.clear();
    }

    @Override
    public void disableBrowseButtons() {
        browseButtonEnabled = false;
    }

    @Override
    public void hideResetButton() {
        resetBtn.hide();
    }

    @Override
    public void finishEditing() {
        input.finishEditing();
    }

    @Override
    public void flush() {
        validate(false);
    }

    @Override
    public List<EditorError> getErrors() {
        return errors;
    }

    public String getInfoText() {
        return infoTextString;
    }

    public List<Validator<String>> getValidators() {
        return input.getValidators();
    }

    @Override
    public R getValue() {
        return model;
    }

    @Override
    public boolean isValid(boolean preventMark) {
        // If the input field is not valid, make a call to validate in order to
        // propagate errors from the input field to the editor delegate.
        if (!input.isValid(preventMark)) {
            validate(preventMark);
        }
        return input.isValid(preventMark);
    }

    public boolean isValidatePermissions() {
        return validatePermissions;
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());

        if (!validateDropStatus(dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());

        if (!validateDropStatus(dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */
    }

    @Override
    public void reset() {
        input.reset();
        model = null;
    }

    /**
     * set id to browse button
     *
     * @param id id to be set for browse button
     */
    public void setBrowseButtonId(String id) {
        button.setId(id);
    }

    @Override
    public void setDelegate(EditorDelegate<R> delegate) {/* Do Nothing */
    }

    public void setEmptyText(String emptyText) {
        input.setEmptyText(emptyText);
    }

    public void setInfoErrorText(String errorMessage) {
        setInfoTextClassName(appearance.errorTextStyle());
        setInfoText(errorMessage);
    }

    /**
     * @param text the text to be shown in the info text element. Passing in null will hide the element.
     */
    public void setInfoText(String text) {
        this.infoTextString = text;
        if (text == null) {
            infoText.getStyle().setDisplay(Display.NONE);
            infoText.setInnerHTML(""); //$NON-NLS-1$
            return;
        }
        // Enable the div
        infoText.getStyle().setWidth(100, Unit.PCT);
        infoText.getStyle().setDisplay(Display.BLOCK);
        // JDS Escape the text as a precaution.
        SafeHtml safeText = SafeHtmlUtils.fromSafeConstant(text);
        infoText.setInnerSafeHtml(safeText);
    }

    public void setInfoTextClassName(String className) {
        infoText.setClassName(className);
    }

    @Override
    public void setRequired(boolean required) {
        input.setAllowBlank(!required);
    }

    /**
     * @param validatePermissions the validatePermissions to set
     */
    public void setValidatePermissions(boolean validatePermissions) {
        this.validatePermissions = validatePermissions;
    }

    @Override
    public void setValue(R value) {
        if ((value == model)) {
            // JDS If model is not changing
            return;
        } else if ((model != null) && (value != null) && model.getPath().equals(value.getPath())) {
            return;
        }
        model = value;
        if (value != null) {
            input.setValue(value.getPath());
            validate(false);
            doGetStat(value);
        }
    }

    @Override
    public void showErrors(List<EditorError> errors) {/* Do Nothing */
    }

    @Override
    public boolean validate(boolean preventMark) {
        errors.clear();
        for (Validator<String> v : input.getValidators()) {
            List<EditorError> errs = v.validate(input, input.getCurrentValue());
            if (errs != null) {
                errors.addAll(errs);
            }
        }
        if (permissionEditorError != null) {
            errors.add(permissionEditorError);
        } else if (existsEditorError != null) {
            errors.add(existsEditorError);
        }
        if (!preventMark) {
            input.showErrors(errors);
        }
        if (errors.size() < 0) {
            input.clearInvalid();
        }
        return !(errors.size() > 0);
    }

    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(resetBtn);
        ComponentHelper.doAttach(input);
        ComponentHelper.doAttach(button);
    }

    @Override
    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(resetBtn);
        ComponentHelper.doDetach(input);
        ComponentHelper.doDetach(button);
    }

    @SuppressWarnings("unchecked")
    protected Set<DiskResource> getDropData(Object data) {
        if (!(data instanceof Collection<?>)) {
            return null;
        }
        Collection<?> dataColl = (Collection<?>) data;
        if (dataColl.isEmpty() || !(dataColl.iterator().next() instanceof DiskResource)) {
            return null;
        }

        return Sets.newHashSet((Collection<DiskResource>) dataColl);
    }

    @Override
    protected XElement getFocusEl() {
        return input.getElement();
    }

    protected boolean isBrowseButtonEnabled() {
        return browseButtonEnabled;
    }

    protected abstract void onBrowseSelected();

    @Override
    protected void onResize(int width, int height) {
       appearance.onResize(width, button.getOffsetWidth(), resetBtn.getOffsetWidth(), input, errorHandler.isShowing());
    }

    protected void setSelectedResource(R selectedResource) {
        setValue(selectedResource);
    }

    abstract protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status);

    private StringBuilder checkForSplChar(String diskResourceId) {
        char[] restrictedChars = (vConstants.warnedDiskResourceNameChars()).toCharArray();
        StringBuilder restrictedFound = new StringBuilder();

        for (char restricted : restrictedChars) {
            for (char next : diskResourceId.toCharArray()) {
                if (next == restricted && next != '/') {
                    restrictedFound.append(restricted);
                }
            }
        }

        // validate '/' only on label
        for (char next : diskResourceUtil.parseNameFromPath(diskResourceId).toCharArray()) {
            if (next == '/') {
                restrictedFound.append('/');
            }
        }

        return restrictedFound;
    }

    private void doGetStat(final R value) {
        final String diskResourcePath = value.getPath();
        HasPaths diskResourcePaths = drServiceFacade.getDiskResourceFactory().pathsList().as();
        diskResourcePaths.setPaths(Lists.newArrayList(diskResourcePath));

        permissionEditorError = null;
        existsEditorError = null;
        FastMap<TYPE> asStringPathTypeMap = diskResourceUtil.asStringPathTypeMap(Arrays.asList(value), TYPE.FILE);
        if (Strings.isNullOrEmpty(value.getPath())
                || asStringPathTypeMap.isEmpty()) {
            // Do not make service call if there are no paths
            return;
        }

        // FIXME No service calls in widgets!!
        drServiceFacade.getStat(asStringPathTypeMap,
                                new AsyncCallback<FastMap<DiskResource>>() {

                                    @Override
                                    public void onFailure(Throwable caught) {

                                        SimpleServiceError serviceError = AutoBeanCodex.decode(drServiceFacade.getDiskResourceFactory(),
                                                                                               SimpleServiceError.class,
                                                                                               caught.getMessage())
                                                                                       .as();
                                        if (serviceError.getErrorCode()
                                                        .equals(ServiceErrorCode.ERR_DOES_NOT_EXIST.toString())) {
                                            existsEditorError = new DefaultEditorError(input,
                                                                                       appearance.diskResourceDoesNotExist(diskResourcePath),
                                                                                       diskResourcePath);
                                            errors.add(existsEditorError);
                                            setInfoErrorText(appearance.diskResourceDoesNotExist(diskResourcePath));
                                            ValueChangeEvent.fire(AbstractDiskResourceSelector.this,
                                                                  value);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(FastMap<DiskResource> result) {
                                        if (!validatePermissions) {
                                            setInfoErrorText("");
                                        } else {
                                            ValueChangeEvent.fire(AbstractDiskResourceSelector.this,
                                                                  value);
                                            DiskResource diskResource = result.get(diskResourcePath);
                                            String infoText = getInfoText();
                                            if (diskResource == null) {
                                                permissionEditorError = new DefaultEditorError(input,
                                                                                               appearance.permissionSelectErrorMessage(),
                                                                                               diskResourcePath);
                                                errors.add(permissionEditorError);
                                                input.showErrors(Lists.<EditorError>newArrayList(permissionEditorError));
                                                setInfoErrorText(appearance.permissionSelectErrorMessage());
                                            } else if (!(diskResourceUtil.isWritable(diskResource) || diskResourceUtil.isOwner(diskResource))) {
                                                permissionEditorError = new DefaultEditorError(input,
                                                                                               appearance.permissionSelectErrorMessage(),
                                                                                               diskResourcePath);
                                                errors.add(permissionEditorError);
                                                input.showErrors(Lists.<EditorError>newArrayList(permissionEditorError));
                                                setInfoErrorText(appearance.permissionSelectErrorMessage());
                                            } else {
                                                if (!Strings.isNullOrEmpty(infoText)
                                                        && (!infoText.equalsIgnoreCase(appearance.nonDefaultFolderWarning()))) {
                                                    // clear only permission related errors on success
                                                    setInfoErrorText(null);
                                                }
                                            }
                                        }

                                        if (checkForSplChar(input.getValue()).length() > 0) {
                                            setInfoErrorText(appearance.analysisFailureWarning(vConstants.warnedDiskResourceNameChars()));
                                        }
                                    }
                                });

    }

    private void initDragAndDrop() {
        DropTarget dataDrop = new DropTarget(this);
        dataDrop.setOperation(Operation.COPY);
        dataDrop.addDragEnterHandler(this);
        dataDrop.addDragMoveHandler(this);
        dataDrop.addDropHandler(this);
    }

    public void setInputFieldId(String id) {
        input.setId(id);
    }
}

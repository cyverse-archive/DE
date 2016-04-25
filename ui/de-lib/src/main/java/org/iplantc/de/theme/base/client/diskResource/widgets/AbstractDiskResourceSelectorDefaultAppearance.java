package org.iplantc.de.theme.base.client.diskResource.widgets;

import org.iplantc.de.commons.client.widgets.IPlantSideErrorHandler;
import org.iplantc.de.diskResource.client.views.widgets.AbstractDiskResourceSelector;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.List;

/**
 * @author jstroot
 */
public class AbstractDiskResourceSelectorDefaultAppearance implements AbstractDiskResourceSelector.SelectorAppearance {
    private final int buttonOffset = 3;
    private static final int OFFSET = 24;
    /**
     * XXX CORE-4671, FYI EXTGWT-1788,2518,3037 have been fixed, and this class MAY no longer be necessary
     *
     * @author jstroot
     */
    private final class DrSideErrorHandler extends IPlantSideErrorHandler {

        private final Widget button1;
        private final Widget resetBtn;
        private final Widget container;
        private final Component input1;


        private DrSideErrorHandler(final Component target,
                                   final Widget container,
                                   final Widget button,
                                   final Widget resetBtn) {
            super(target);
            this.input1 = target;
            this.container = container;
            this.button1 = button;
            this.resetBtn = resetBtn;
        }

        @Override
        public void clearInvalid() {
            super.clearInvalid();
            int offset = button1.getOffsetWidth() + buttonOffset + +resetBtn.getOffsetWidth();
            input1.setWidth(container.getOffsetWidth() - offset);
        }

        @Override
        public void markInvalid(List<EditorError> errors) {
            super.markInvalid(errors);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    if (isShowing()) {
                        int offset = button1.getOffsetWidth() + buttonOffset + OFFSET
                                         + resetBtn.getOffsetWidth();
                        input1.setWidth(container.getOffsetWidth() - offset);
                    }
                }
            });
        }
    }

    interface FileFolderSelectorStyle extends CssResource {
        String errorText();
    }

    public interface FileUploadTemplate extends XTemplates {
        @XTemplate("<table width='100%' height='100%'><tbody><tr><td class='cell1' /><td class='cell2'/><td class='cell3'/></tr></tbody></table>")
        SafeHtml
        render();
    }

    interface Resources extends ClientBundle {
        @Source("org/iplantc/de/resources/client/arrow_undo.png")
        ImageResource arrowUndo();

        @Source("AbstractDiskResourceSelector.css")
        FileFolderSelectorStyle style();
    }

    private final Resources resources;
    private final FileFolderSelectorStyle style;
    private final FileUploadTemplate uploadTemplate;
    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;

    public AbstractDiskResourceSelectorDefaultAppearance() {
        this(GWT.<Resources> create(Resources.class),
             GWT.<FileUploadTemplate> create(FileUploadTemplate.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    AbstractDiskResourceSelectorDefaultAppearance(final Resources resources,
                                                  final FileUploadTemplate uploadTemplate,
                                                  final DiskResourceMessages diskResourceMessages,
                                                  final IplantDisplayStrings iplantDisplayStrings) {
        this.resources = resources;
        this.uploadTemplate = uploadTemplate;
        this.diskResourceMessages = diskResourceMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        style = resources.style();
        style.ensureInjected();
    }

    @Override
    public String analysisFailureWarning(String s) {
        return iplantDisplayStrings.analysisFailureWarning(s);
    }

    @Override
    public ImageResource arrowUndo() {
        return resources.arrowUndo();
    }

    @Override
    public String browse() {
        return diskResourceMessages.browse();
    }

    @Override
    public String cell1() {
        return ".cell1";
    }

    @Override
    public String cell2() {
        return ".cell2";
    }

    @Override
    public String cell3() {
        return ".cell3";
    }

    @Override
    public String diskResourceDoesNotExist(String diskResourcePath) {
        return diskResourceMessages.diskResourceDoesNotExist(diskResourcePath);
    }

    @Override
    public String errorTextStyle() {
        return style.errorText();
    }

    @Override
    public IPlantSideErrorHandler getSideErrorHandler(TextField input,
                                                      Widget container,
                                                      Widget button, Widget resetBtn) {
        return new DrSideErrorHandler(input, container, button, resetBtn);
    }

    @Override
    public String nonDefaultFolderWarning() {
        return diskResourceMessages.nonDefaultFolderWarning();
    }

    @Override
    public void onResize(int width,
                         int offsetWidth1,
                         int offsetWidth2,
                         Component input,
                         boolean errorHandlerShowing) {
        int offset = offsetWidth1 + buttonOffset * 2 + offsetWidth2;
        if (errorHandlerShowing) {
            offset += OFFSET;
        }
        input.setWidth(width - offset);
    }

    @Override
    public String permissionSelectErrorMessage() {
        return diskResourceMessages.permissionSelectErrorMessage();
    }

    @Override
    public SafeHtml renderTable() {
        return uploadTemplate.render();
    }

    @Override
    public SafeHtml dataDragDropStatusText(int size) {
        return diskResourceMessages.dataDragDropStatusText(size);
    }
}

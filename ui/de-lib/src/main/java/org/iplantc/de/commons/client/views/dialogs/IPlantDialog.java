package org.iplantc.de.commons.client.views.dialogs;

import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.commons.client.widgets.ContextualHelpToolButton;
import org.iplantc.de.commons.share.CommonsModule;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.ArrayList;

/**
 * A base class for GXT 3.x IPlant dialogs. All IPlant dialogs will be modal windows.
 * 
 * @author jstroot
 *
 * KLUDGE CORE-5583 Needs to be refactored, changes made only to correct build errors
 */
public class IPlantDialog extends Dialog implements IsHideable, IsMaskable {

    protected ContextualHelpToolButton helpTool;

    private final ArrayList<SelectHandler> okButtonSelectHandlers = new ArrayList<>();
    private final ArrayList<SelectHandler> cancelButtonSelectHandlers = new ArrayList<>();
    
    public IPlantDialog() {
        // no contextual help tool icon by default
        this(false);
    }

    public IPlantDialog(boolean contextualHelpTool) {
        if (contextualHelpTool) {
            helpTool = new ContextualHelpToolButton();
            getHeader().addTool(helpTool);
        }
        init();
    }
    
    /**
     * Add help to dialog
     * 
     * @param c widget containing help text
     * 
     */
    public void addHelp(Widget c) {
        helpTool.setHelp(c);
    }

    protected void init() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setModal(true);
        setResizable(false);
        setHideOnButtonClick(true);
    }

    @Override
    protected void onButtonPressed(TextButton button) {
        if (button == getButtonBar().getItemByItemId(PredefinedButton.OK.name())) {
            onOkButtonClicked();
            callEventHandlers(okButtonSelectHandlers, button);
        } else if (button == getButtonBar().getItemByItemId(PredefinedButton.CANCEL.name())) {
            callEventHandlers(cancelButtonSelectHandlers, button);
        }
        super.onButtonPressed(button);
    }

    protected void onOkButtonClicked() {

    }

    public ToolButton getHelpToolButton() {
        return helpTool;
    }

    @Editor.Ignore
    public TextButton getOkButton() {
        Widget okButton = getButtonBar().getItemByItemId(PredefinedButton.OK.name());
        if ((okButton != null) && (okButton instanceof TextButton)) {
            return (TextButton)okButton;
        }
        return null;
    }

    public void addOkButtonSelectHandler(final SelectHandler handler) {
        okButtonSelectHandlers.add(handler);
    }

    public void addCancelButtonSelectHandler(final SelectHandler handler) {
        cancelButtonSelectHandlers.add(handler);
    }

    private void callEventHandlers(final ArrayList<SelectHandler> handlers, final TextButton button) {
        for (SelectHandler handler : handlers) {
            handler.onSelect(new SelectEvent(new Context(0, 0, button)));
        }
    }

    public void setOkButtonText(String text) {
        getOkButton().setText(text);
    }

    /**
     * Remove all button handlers.
     */
    public void clearHandlers() {
        okButtonSelectHandlers.clear();
        cancelButtonSelectHandlers.clear();
    }

    @Override
    /**
     * NOTE: The IPlantDialog ensureDebugId() method should be called AFTER the show() method in order
     * for the Close button to be accessed and given a debug id.
     */
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        if (helpTool != null) {
            helpTool.ensureDebugId(baseID + CommonsModule.IDs.HELP);
        }

        TextButton cancelBtn = getButton(PredefinedButton.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.ensureDebugId(baseID + CommonsModule.IDs.CANCEL);
        }

        if (closeBtn != null) {
            closeBtn.ensureDebugId(baseID + CommonsModule.IDs.CLOSE);
        }
        
    }
}

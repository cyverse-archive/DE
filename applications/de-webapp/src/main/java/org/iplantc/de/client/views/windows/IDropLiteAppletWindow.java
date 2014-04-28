package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.idroplite.presenter.IDropLitePresenter;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.idroplite.views.IDropLiteView;
import org.iplantc.de.client.idroplite.views.IDropLiteView.Presenter;
import org.iplantc.de.client.idroplite.views.IDropLiteViewImpl;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.commons.client.views.window.configs.IDropLiteWindowConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * @author sriram
 * 
 */
public class IDropLiteAppletWindow extends IplantWindowBase {

    private final IDropLiteWindowConfig idlwc;

    public IDropLiteAppletWindow(IDropLiteWindowConfig config) {
        super("");
        this.idlwc = config;
        setSize("850", "430");
        setResizable(false);
        init();
    }

    private void init() {
        // These settings enable the window to be minimized or moved without reloading the applet.
        removeFromParentOnHide = false;
        setHideMode(HideMode.VISIBILITY);

        initViewMode();
        initListeners();

        IDropLiteView view = new IDropLiteViewImpl();
        Presenter p = new IDropLitePresenter(view, idlwc);
        p.go(this);

        checkAndWarn();
    }

    private void checkAndWarn() {
        boolean isOSX = GXT.isMac();
        boolean isChrome = GXT.isChrome();
        if (isOSX && isChrome) {
            final IplantInfoBox iib = new IplantInfoBox(I18N.DISPLAY.warning(), "Bulk operations may not work as intented with Chrome browser on OS X. Please use Safari or Firefox browser.");
            Scheduler.get().scheduleFinally(new ScheduledCommand() {

                @Override
                public void execute() {
                    iib.show();
                }
            });

        }
    }

    private void initListeners() {
        if (GXT.isWindows()) {
            // In Windows, the applet always stays on top, blocking access to everything else.
            // So minimize this window if it loses focus.
            addDeactivateHandler(new DeactivateHandler<Window>() {

                @Override
                public void onDeactivate(DeactivateEvent<Window> event) {
                    minimize();
                }
            });
        }
    }

    private int initViewMode() {
        // Set the heading and add the correct simple mode button based on the applet display mode.
        int displayMode = idlwc.getDisplayMode();
        if (displayMode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.upload());

        } else if (displayMode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.download());
        }

        return displayMode;

    }

    protected void confirmHide() {
        super.doHide();

        // refresh manage data window
        Folder currentFolder = idlwc.getCurrentFolder();
        if (currentFolder != null) {
            FolderRefreshEvent event = new FolderRefreshEvent(currentFolder);
            EventBus.getInstance().fireEvent(event);
        }
    }

    @Override
    protected void doHide() {
        promptRemoveApplet(new Command() {
            @Override
            public void execute() {
                confirmHide();
            }
        });
    }

    private void promptRemoveApplet(final Command cmdRemoveAppletConfirmed) {
        if (GXT.isWindows()) {
            // In Windows, the applet always stays on top, blocking access to the confirmation dialog,
            // which is modal and blocks access to everything else.
            minimize();
        }

        final ConfirmMessageBox cmb = new ConfirmMessageBox(org.iplantc.de.resources.client.messages.I18N.DISPLAY.idropLiteCloseConfirmTitle(),
                org.iplantc.de.resources.client.messages.I18N.DISPLAY.idropLiteCloseConfirmMessage());

        cmb.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                if (GXT.isWindows()) {
                    show();
                }

                if (cmb.getHideButton().getText().equalsIgnoreCase("yes")) {
                    // The user confirmed closing the applet.
                    cmdRemoveAppletConfirmed.execute();
                }

            }
        });

        cmb.show();
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(idlwc);
    }

}

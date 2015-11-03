package org.iplantc.de.systemMessages.client.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.services.SystemMessageServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.AnnouncementId;
import org.iplantc.de.commons.client.info.AnnouncementRemovedEvent;
import org.iplantc.de.commons.client.info.IplantAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.systemMessages.client.events.NewSystemMessagesEvent;
import org.iplantc.de.systemMessages.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.systemMessages.client.view.Factory;
import org.iplantc.de.systemMessages.client.view.NewMessageView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

/**
 * An object of this class manages the interactions of a NewMessageView view.
 * 
 * When an object of this class is no longer needed, the object's tearDown() method should be called
 * to free resources.
 */
public final class NewMessagePresenter implements NewMessageView.Presenter {

    private static final SystemMessageServiceFacade SERVICES = ServicesInjector.INSTANCE.getSystemMessageServiceFacade();
    private static final Factory VIEW_FACTORY = GWT.create(Factory.class);
    
    private final NewMessageView view;
    private final EventBus eventBus;
    private final IplantAnnouncer announcer;
    private final IplantAnnouncementConfig annCfg;
    private final HandlerRegistration arrivalReg;

    private AnnouncementId currentAnnId;
    private HandlerRegistration removalReg;

    /**
     * the constructor
     * 
     * @param eventBus the event bus used by the application
     * @param announcer the particular announcer that will contain the view managed by this presenter.
     */
    @Inject
    public NewMessagePresenter(final EventBus eventBus, final IplantAnnouncer announcer) {
        view = VIEW_FACTORY.makeNewMessageView(this);
        this.eventBus = eventBus;
        this.announcer = announcer;
        annCfg = new SysMsgAnnouncementConfig(view);
        currentAnnId = null;
        removalReg = null;
        arrivalReg = eventBus.addHandler(NewSystemMessagesEvent.TYPE, new NewSystemMessagesEvent.Handler() {
            @Override
            public void onUpdate(final NewSystemMessagesEvent event) {
                announceArrival();
            }
        });
    }

    /**
     * Releases resources consumed by the object. This should be called when the object is no
     * longer needed
     */
    public void tearDown() {
        arrivalReg.removeHandler();
        if (removalReg != null) {
            removalReg.removeHandler();
        }
    }

    /**
     * @see Object#finalize()
     */
    @Override
    protected void finalize() {
        try {
            tearDown();
            super.finalize();
        } catch (final Throwable e) {}
    }

    /**
     * @see NewMessageView.Presenter#handleDisplayMessages()
     */
    @Override
    public void handleDisplayMessages() {
        eventBus.fireEvent(new ShowSystemMessagesEvent());
        announcer.unschedule(currentAnnId);
    }

    private void announceArrival() {
        if (!isAnnouncementScheduled()) {
            currentAnnId = announcer.schedule(annCfg);
            removalReg = eventBus.addHandler(AnnouncementRemovedEvent.TYPE, new AnnouncementRemovedEvent.Handler() {
                @Override
                public void onRemove(final AnnouncementRemovedEvent event) {
                    handleAnnouncementRemovalEvent(event);
                }
            });
        }
    }

    private void handleAnnouncementRemovalEvent(final AnnouncementRemovedEvent event) {
        if (event.getAnnouncement().equals(currentAnnId)) {
            if (event.wasAnnounced()) {
                SERVICES.markAllReceived(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable cause) {
                        ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.markMessageReceivedFailed(), cause);
                        finishRemoval();
                    }
                    @Override
                    public void onSuccess(Void unused) {
                        finishRemoval();
                    }
                });
            } else {
                finishRemoval();
            }
        }
    }

    private void finishRemoval() {
        removalReg.removeHandler();
        removalReg = null;
        currentAnnId = null;
    }

    private boolean isAnnouncementScheduled() {
        return currentAnnId != null;
    }

    private final class SysMsgAnnouncementConfig extends IplantAnnouncementConfig {
        private final IsWidget view;

        public SysMsgAnnouncementConfig(IsWidget view) {
            super(null, true, 0);

            this.view = view;
        }

        @Override
        public IsWidget getWidget() {
            return view;
        }
    }
}

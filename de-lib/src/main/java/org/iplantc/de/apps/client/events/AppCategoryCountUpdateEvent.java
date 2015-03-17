package org.iplantc.de.apps.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class AppCategoryCountUpdateEvent extends GwtEvent<AppCategoryCountUpdateEvent.AppCategoryCountUpdateEventHandler> {

    public static final GwtEvent.Type<AppCategoryCountUpdateEventHandler> TYPE = new GwtEvent.Type<>();

    /**
     * Describes the count update type
     * 
     * How the widget treats the enum values:
     * <dl>
     * <dt>FAVORITES</dt>
     * <dd>Informs the event handler to update the Favorites category count.</dd>
     * <dt>BETA</dt>
     * <dd>Informs the event handler to also update the Beta category count.</dd>
     * </dl>
     * 
     * @author psarando
     * 
     */
    public enum AppCategoryType {
        /**
         * The favorites category should be incremented instead of the user apps category.
         */
        FAVORITES,

        /**
         * The beta category should also be incremented.
         */
        BETA
    }

    private boolean increment;
    private AppCategoryType appCategoryType;
  
    public AppCategoryCountUpdateEvent(boolean inc, AppCategoryType appCategoryType) {
        setIncrement(inc);
        setAppCategoryType(appCategoryType);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AppCategoryCountUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppCategoryCountUpdateEventHandler handler) {
        handler.onGroupCountUpdate(this);
    }

    /**
     * @param inc boolean suggesting if its an increment
     */
    public void setIncrement(boolean inc) {
        this.increment = inc;
    }

    /**
     * @param groupType the fav_event to set
     */
    public void setAppCategoryType(AppCategoryType groupType) {
        this.appCategoryType = groupType;
    }

    /**
     * @return the GroupType
     */
    public AppCategoryType getAppCategoryType() {
        return appCategoryType;
    }

    public static interface AppCategoryCountUpdateEventHandler extends EventHandler {
        void onGroupCountUpdate(AppCategoryCountUpdateEvent event);
    }
}

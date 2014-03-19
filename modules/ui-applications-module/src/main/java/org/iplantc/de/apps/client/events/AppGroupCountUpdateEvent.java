package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.handlers.AppGroupCountUpdateEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class AppGroupCountUpdateEvent extends GwtEvent<AppGroupCountUpdateEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.apps.client.events.handlers.AppGroupCountUpdateEventHandler.AppGroupCountUpdateEventHandler
     */
    public static final GwtEvent.Type<AppGroupCountUpdateEventHandler> TYPE = new GwtEvent.Type<AppGroupCountUpdateEventHandler>();

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
    public enum AppGroupType {
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
    private AppGroupType groupType;
  
    public AppGroupCountUpdateEvent(boolean inc, AppGroupType groupType) {
        setIncrement(inc);
        setAppGroupType(groupType);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AppGroupCountUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppGroupCountUpdateEventHandler handler) {
        handler.onGroupCountUpdate(this);
    }

    /**
     * @param inc boolean suggesting if its an increment
     */
    public void setIncrement(boolean inc) {
        this.increment = inc;
    }

    /**
     * @return the boolean suggesting if its an increment
     */
    public boolean isIncrement() {
        return increment;
    }

    /**
     * @param groupType the fav_event to set
     */
    public void setAppGroupType(AppGroupType groupType) {
        this.groupType = groupType;
    }

    /**
     * @return the GroupType
     */
    public AppGroupType getAppGroupType() {
        return groupType;
    }

}

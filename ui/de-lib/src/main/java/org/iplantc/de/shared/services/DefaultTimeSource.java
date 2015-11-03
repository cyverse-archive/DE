package org.iplantc.de.shared.services;

import java.util.Date;


/**
 * Generates the current time using the default Date() constructor.
 */
public final class DefaultTimeSource implements ProvidesTime {

    /**
     * @see ProvidesTime#now()
     */
    @Override
    public Date now() {
        return new Date();
    }

}

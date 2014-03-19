package org.iplantc.de.shared;

import java.util.Date;

/**
 * Implementations of this interface can provide the current time.
 * 
 * This interface exists so that unit tests can provide known times.
 */
public interface ProvidesTime {

    /**
     * provides the current time
     * 
     * @return the current time
     */
    Date now();

}

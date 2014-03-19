package org.iplantc.de.client.services;

import com.google.gwt.i18n.client.Constants;

/**
 * This interface provides access to all reserved keys in the "reserved" bucket, which is accessed by the
 * <code>buckets</code> endpoint which may be <a
 * href="https://github.com/iPlantCollaborativeOpenSource/Donkey/blob/dev/doc/endpoints/misc.md"
 * >here</a>.
 * 
 * @author jstroot
 * 
 */
public interface ReservedBuckets extends Constants {

    String queryTemplates();

}

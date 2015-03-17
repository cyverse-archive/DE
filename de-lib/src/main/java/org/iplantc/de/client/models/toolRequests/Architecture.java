package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * These are the definitions of architectures a tool may run on.
 */
public enum Architecture {

    @PropertyName("32-bit Generic")
    GENERIC_32,

    @PropertyName("64-bit Generic")
    GENERIC_64,

    @PropertyName("Others")
    VM_OR_INTERPRETED,

    @PropertyName("Don't know")
    UNKNOWN;

}

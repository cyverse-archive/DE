package org.iplantc.de.apps.client;

import org.iplantc.de.commons.client.CommonUiConstants;

import com.google.gwt.core.client.GWT;

/**
 * Static access to client constants.
 * 
 * @author hariolf
 * 
 */
public class Constants {
    /** CommonConstants, auto-populated from .properties by GWT */
    public static final CommonUiConstants CLIENT = GWT.create(CommonUiConstants.class);
}

/**
 *
 */
package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.viewer.VizUrl;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 */
public interface VizUrlProperties extends PropertyAccess<VizUrl> {

    ValueProvider<VizUrl, String> label();

    ValueProvider<VizUrl, String> url();

}

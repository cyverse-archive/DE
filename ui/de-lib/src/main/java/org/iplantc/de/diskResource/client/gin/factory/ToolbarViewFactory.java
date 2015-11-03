package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.diskResource.client.ToolbarView;

/**
 * Created by jstroot on 1/30/15.
 * @author jstroot
 */
public interface ToolbarViewFactory {
    ToolbarView create(ToolbarView.Presenter presenter);
}

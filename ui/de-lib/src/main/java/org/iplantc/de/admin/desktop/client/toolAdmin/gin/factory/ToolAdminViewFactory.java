package org.iplantc.de.admin.desktop.client.toolAdmin.gin.factory;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.Tool;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public interface ToolAdminViewFactory {
    ToolAdminView create(ListStore<Tool> listStore);
}

package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.HasId;

public interface AppsWindowConfig extends WindowConfig {

    HasId getSelectedAppCategory();

    HasId getSelectedApp();

    void setSelectedAppCategory(HasId appGroup);

    void setSelectedApp(HasId app);

}

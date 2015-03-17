package org.iplantc.de.apps.client.models;

import org.iplantc.de.client.models.apps.AppCategory;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author jstroot
 */
public class AppCategoryStringValueProvider implements ValueProvider<AppCategory, String> {

    @Override
    public String getValue(AppCategory object) {
        return object.getName() + " (" + object.getAppCount() + ")";
    }

    @Override
    public void setValue(AppCategory object, String value) {
        // do nothing intentionally
    }

    @Override
    public String getPath() {
        return null;
    }
}

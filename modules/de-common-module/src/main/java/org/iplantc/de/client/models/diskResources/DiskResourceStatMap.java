package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Map;

public interface DiskResourceStatMap {

    final class Category {
        public static DiskResource get(final AutoBean<DiskResourceStatMap> instance, final String resourcePath) {
            return instance.as().getMap().get(resourcePath);
        }

        private Category() {}
    }

    /**
     * Given the resource path, it returns the corresponding stats.
     * 
     * @param resourcePath the path to the resource
     * 
     * @return the <code>DiskResource</code> associated with the given path.
     */
    DiskResource get(String resourcePath);

    /**
     * Converts the object to a Map
     * 
     * @return the object as a Map
     */
    @PropertyName("paths")
    Map<String, DiskResource> getMap();

}

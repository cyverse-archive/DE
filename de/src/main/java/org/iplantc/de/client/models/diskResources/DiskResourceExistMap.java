package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Map;

/**
 * Objects of this class map a disk resource path to a boolean indicating whether or not the
 * resource exists.
 */
public interface DiskResourceExistMap {

    final class Category {
        public static boolean get(final AutoBean<DiskResourceExistMap> instance, final String resourcePath) {
            return instance.as().getMap().get(resourcePath);
        }

        private Category() {}
    }

    /**
     * Given the resource path, it returns true if the resource existed at the time of the request
     * and false if it did not.
     * 
     * @param resourcePath the path to the resource
     * 
     * @return true if the resource existed, otherwise false.
     */
    boolean get(String resourcePath);

    /**
     * Converts the object to a Map
     * 
     * @return the object as a Map
     */
    @PropertyName("paths")
    Map<String, Boolean> getMap();

}

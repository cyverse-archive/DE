package org.iplantc.de.client.models.errorHandling;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

/**
 * An object representing the basic structure of error messages returned from service endpoints.
 * 
 * Ideally, this interface will be extended to create type-safe objects representing individual error
 * messages.
 * 
 * @author jstroot
 * 
 */
public interface ServiceError extends SimpleServiceError {

    /**
     * A Non-property method which must be implemented with a {@link Category} method.
     * 
     * @return
     */
    SafeHtml generateErrorMsg();

}

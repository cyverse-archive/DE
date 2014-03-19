package org.iplantc.de.server;

import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Resolves service calls from the client usage of a service key to the actual service address, or URL.
 *
 */
public abstract class ServiceCallResolver {

    /**
     * Resolves the wrapper information to a service address, or URL.
     *
     * @param wrapper service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if a service name that couldn't be resolved is passed to the resolver.
     */
    public abstract String resolveAddress(BaseServiceCallWrapper wrapper);

    /**
     * Resolves a service call for a specific service name.
     *
     * @param serviceName the service name.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if the service name can't be resolved.
     */
    public abstract String resolveAddress(String serviceName);

    /**
     * Gets the service call resolver for a servlet context.
     *
     * @param context the servlet context.
     * @return the service call resolver.
     * @throws IllegalStateException if the service call resolver can't be found.
     */
    public static ServiceCallResolver getServiceCallResolver(ServletContext context) {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        ServiceCallResolver resolver = appContext.getBean(ServiceCallResolver.class);
        if (resolver == null) {
            throw new IllegalStateException("no service call resolver bean defined");
        }
        return resolver;
    }
}

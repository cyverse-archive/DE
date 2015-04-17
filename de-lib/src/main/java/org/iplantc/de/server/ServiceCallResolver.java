package org.iplantc.de.server;

import org.iplantc.de.shared.exceptions.UnresolvableServiceNameException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jstroot
 */
@Component
public class ServiceCallResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCallResolver.class);
    private static final String PREFIX_KEY = "prefix";

    private Environment environment;

    @Autowired
    public void setAppProperties(Environment environment) {
        LOG.trace("Set app properties");
        this.environment = environment;
        setPrefix();
        validatePrefix();
    }

//    private Properties appProperties;
    private String prefix;

    private void validatePrefix() {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Properties argument must contain a property defining "
                    + "the prefix for service keys: " + PREFIX_KEY);
        }
    }

    private void setPrefix() {
        prefix = environment.getProperty(PREFIX_KEY);
    }

    /**
     * Resolves a service call to a valid service address.
     *
     * This implementation determines if the wrapper contains a "service key" instead of the actual
     * service address. If so, the service key is resolved with the properties. Otherwise, the wrapper's
     * address is passed through without change.
     *
     * @param wrapper service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if a service name that couldn't be resolved is passed to the resolver.
     */
    public String resolveAddress(BaseServiceCallWrapper wrapper) {
        return resolveAddress(wrapper.getAddress());
    }

    /**
     * Resolves a service call for a specific service name.
     *
     * @param serviceName the service name.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if the service name can't be resolved.
     */
    public String resolveAddress(String serviceName) {
        NamedServiceCall serviceCall = NamedServiceCall.parse(prefix, serviceName);
        final String retVal = serviceCall == null ? serviceName : serviceCall.resolve(environment);
        LOG.debug("\"{}\" resolved to: {}", serviceName, retVal);
        return retVal;
    }

    /**
     * Represents a named service call that can be resolved against a set of properties.
     */
    private static class NamedServiceCall {

        private static final Logger LOG = LoggerFactory.getLogger(NamedServiceCall.class);
        /**
         * The name of the service.
         */
        private String serviceName;

        /**
         * Any additional path components to add to the resolved URL, if applicable.
         */
        private String additionalPath;

        /**
         * The query string to add to the resolved URL, if applicable.
         */
        private String query;

        /**
         * @return the additional path components to add to the URL or the empty string.
         */
        private String getAdditionalPath() {
            return additionalPath == null ? "" : additionalPath;
        }

        /**
         * @return the query string to add to the URL or the empty string.
         */
        private String getQuery() {
            return query == null ? "" : query;
        }

        /**
         * @param serviceName the name of the service.
         * @param additionalPath any additional path components to add to the resolved URL, may be null.
         * @param query the query string to add to the resolved URL, may be null.
         */
        private NamedServiceCall(String serviceName, String additionalPath, String query) {
            this.serviceName = serviceName;
            this.additionalPath = additionalPath;
            this.query = query;
            LOG.trace("Constructor args:\n\t" +
                         "serviceName = {}\n\t" +
                         "additionalPath = {}\n\t" +
                         "query = {}", serviceName, additionalPath, query);
        }

        /**
         * Parses an address into a named service call.  If the address appears to correspond to a named service
         * call then a new NamedServiceCall will be returned.  Otherwise, null will be returned.
         *
         * @param prefix the property name prefix for named service calls.
         * @param address the address to convert.
         * @return the NamedServiceCall instance or null if the address doesn't represent a named service call.
         */
        public static NamedServiceCall parse(String prefix, String address) {
            Pattern pattern = Pattern.compile("(\\Q" + prefix + "\\E[^/?]+)(/[^?]*)?(\\?.*)?");
            Matcher matcher = pattern.matcher(address);
            if (matcher.matches()) {
                return new NamedServiceCall(matcher.group(1), matcher.group(2), matcher.group(3));
            }
            else {
                LOG.debug("Parsing failed for;\n\t" +
                              "prefix = {}\n\t" +
                              "address = {}", prefix, address);
                return null;
            }
        }

        /**
         * Resolves a named service call.
         *
         * @param props the properties to use when resolving the call.
         * @return the resolved URL.
         * @throws UnresolvableServiceNameException if the service name isn't found in the properties.
         */
        public String resolve(Environment props) {
            final String retVal = getServiceBaseUrl(props) + getAdditionalPath() + getQuery();
            LOG.trace("RESOLVED\n\t" +
                          "service name: {}\n\t" +
                          "to: {}", serviceName, retVal);
            return retVal;
        }

        /**
         * Gets the base URL for this named service call.
         *
         * @param props the properties to use when resolving the service call.
         * @return the base URL to use when connecting to the service.
         * @throws UnresolvableServiceNameException if the service name isn't found in the properties.
         */
        private String getServiceBaseUrl(Environment props) {
            String result = props.getProperty(serviceName);
            if (result == null) {
                LOG.error("unknown service name: {}", serviceName);
//                if (LOG.isDebugEnabled()) {
//                    for (String prop : new TreeSet<>(props.stringPropertyNames())) {
//                        LOG.debug("configuration setting: {} = {}", prop, props.getProperty(prop));
//                    }
//                }
                throw new UnresolvableServiceNameException(serviceName);
            }
            return result;
        }
    }
}

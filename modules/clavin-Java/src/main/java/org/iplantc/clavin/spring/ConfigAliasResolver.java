package org.iplantc.clavin.spring;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * Resolves aliased configurations names.
 *
 * @author Dennis Roberts
 */
public class ConfigAliasResolver {

    /**
     * The configurer that stores the actual configurations.
     */
    private ClavinPropertyPlaceholderConfigurer configurer;

    /**
     * Maps configuration aliases to their actual names.
     */
    private Map<String, String> aliases;

    /**
     * @param configurer the configurer that stores the actual configurations.
     */
    public void setConfigurer(ClavinPropertyPlaceholderConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * @param aliases maps configuration aliases to their actual names.
     */
    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    /**
     * Retrieves an aliased configuration.
     *
     * @param alias the alias.
     * @return the configuration or null if the configuration isn't found.
     */
    public Properties getAliasedConfig(String alias) {
        String name = aliases.get(alias);
        return name == null ? null : configurer.getConfig(name);
    }

    /**
     * Retrieves an aliased configuration, throwing an {@link IllegalStateException} if a configuration with the
     * given alias can't be found.
     *
     * @param alias the configuration alias.
     * @return the configuration.
     * @throws IllegalStateException if the configuration isn't found.
     */
    public Properties getRequiredAliasedConfig(String alias) {
        Properties result = getAliasedConfig(alias);
        if (result == null) {
            throw new IllegalStateException("no configuration with alias, " + alias + ", found");
        }
        return result;
    }

    /**
     * Retrieves an aliased configuration for a servlet context.
     *
     * @param context the servlet context.
     * @param alias the configuration alias.
     * @return the configuration or null if the configuration isn't found.
     */
    public static Properties getAliasedConfigFrom(ServletContext context, String alias) {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        ConfigAliasResolver resolver = appContext.getBean(ConfigAliasResolver.class);
        return resolver.getAliasedConfig(alias);
    }

    /**
     * Retrieves an aliased configuration for a servlet context, throwing an {@link IllegalStateException} if a
     * configuration with the given alias isn't found.
     *
     * @param context the servlet context.
     * @param alias the configuration alias.
     * @return the configuration.
     * @throws IllegalStateException if the configuration isn't found.
     */
    public static Properties getRequiredAliasedConfigFrom(ServletContext context, String alias) {
        Properties result = getAliasedConfigFrom(context, alias);
        if (result == null) {
            throw new IllegalStateException("no configuration with alias, " + alias + ", found");
        }
        return result;
    }
}

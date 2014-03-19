package org.iplantc.de.server;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dennis
 */
public class CasGroupUserDetailsService extends AbstractCasAssertionUserDetailsService {

    /**
     * Used to log error and informational messages.
     */
    private static final Logger LOG = Logger.getLogger(CasGroupUserDetailsService.class);

    /**
     * The pattern used to extract list contents from the string representation of a list.
     */
    private static final Pattern LIST_CONTENTS_PATTERN = Pattern.compile("\\A\\[([^\\]]*)\\]\\z");

    /**
     * The pattern used to separate list elements in the string representation of a list.
     */
    private static final Pattern LIST_DELIMITER_PATTERN = Pattern.compile(",\\s*");

    /**
     * The value to use for the user's password in all cases.
     */
    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";

    /**
     * The name of the attribute containing the groups the user belongs to.
     */
    private final String attribute;

    /**
     * @param attribute the name of the attribute containing the groups the user belongs to.
     */
    private CasGroupUserDetailsService(String attribute) {
        Assert.isTrue(!StringUtils.isBlank(attribute), "attribute must have a non-empty value");
        this.attribute = attribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        final Object value = assertion.getPrincipal().getAttributes().get(attribute);
        if (value != null) {
            for (String groupName : convertToList(value.toString())) {
                grantedAuthorities.add(new SimpleGrantedAuthority(groupName));
            }
        }
        String name = assertion.getPrincipal().getName();
        LOG.debug("Granted Authorities: " + grantedAuthorities);
        return new User(name, NON_EXISTENT_PASSWORD_VALUE, true, true, true, true, grantedAuthorities);
    }

    /**
     * Converts a string representation of a list to a list of strings.
     * 
     * @param listString the string representation of the list.
     * @return the list of strings.
     */
    private List<String> convertToList(String listString) {
        return Arrays.asList(LIST_DELIMITER_PATTERN.split(extractListContents(listString)));
    }

    /**
     * Extracts the list contents string from a string representation of a list.
     *
     * @param listString the string representation of the list.
     * @return the list contents or null
     */
    public String extractListContents(String listString) {
        String result = "";
        if (listString != null) {
            Matcher m = LIST_CONTENTS_PATTERN.matcher(listString);
            result = m.find() ? m.group(1) : "";
        }
        return result;
    }
}

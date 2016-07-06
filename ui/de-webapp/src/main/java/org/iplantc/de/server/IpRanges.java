package org.iplantc.de.server;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of IP address ranges.
 *
 * @author Dennis Roberts
 */
public class IpRanges {
    private final List<IpRange> ranges;

    /**
     * Constructs a set of IP address ranges from a comma-delimited string.
     *
     * @param s the comma-delimited string.
     */
    public IpRanges(String s) {
        List<IpRange> ranges = new ArrayList<IpRange>();
        for (String r : s.split("\\s*,\\s*")) {
            if (!StringUtils.isBlank(r)) {
                ranges.add(new IpRange(r));
            }
        }
        this.ranges = ranges;
    }

    /**
     * Determines whether or not an IP address matches one of the ranges in the set.
     *
     * @param address the IP address.
     * @return true if the address matches one of the ranges.
     */
    public boolean matches(String address) {
        for (IpRange r : ranges) {
            if (r.matches(address)) {
                return true;
            }
        }
        return false;
    }
}

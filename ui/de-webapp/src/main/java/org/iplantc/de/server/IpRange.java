package org.iplantc.de.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a range of IP addresses.
 *
 * @author Dennis Roberts
 */
public class IpRange {

    // The regular expression used to validate address ranges.
    private final static Pattern rangeRe
            = Pattern.compile("^(\\d{1,3}(?:[.]\\d{1,3}){3})(?:/(\\d{1,2}))?$");

    // The regular expression used to validated addresses.
    private final static Pattern addressRe = Pattern.compile("^(\\d{1,3}(?:[.]\\d{1,3}){3})$");

    private final long baseAddress;
    private final long mask;

    /**
     * Creates a new IP Address range based on a string in the format ip/significantBits. For
     * example: 127.0.0.1/24.
     *
     * @param s the string representing the IP address range.
     */
    public IpRange(String s) {

        // Validate the string format.
        Matcher m = rangeRe.matcher(s);
        if (!m.find()) {
            illegalIpRangeString(s);
        }

        // Extract and validate the number of significant bits.
        int significantBits;
        if (m.group(2) != null) {
            significantBits = Integer.parseInt(m.group(2));
            if (significantBits > 32) {
                illegalIpRangeString(s);
            }
        } else {
            significantBits = 32;
        }

        // Initialize the properties.
        baseAddress = addressToInt(m.group(1));
        mask = significantBitsToMask(significantBits);
    }

    /**
     * Throws an exception indicating that an illegal IP address range string was specified.
     *
     * @param s the IP address range string.
     */
    private void illegalIpRangeString(String s) {
        throw new IllegalArgumentException("Invalid IP address range string: " + s);
    }

    /**
     * Throws an exception indicating that an illegal IP address string was specified.
     *
     * @param s the IP address string.
     */
    private void illegalIpAddressString(String s) {
        throw new IllegalArgumentException("Invalid IP address string: " + s);
    }

    /**
     * Converts an IP address string to a long integer.
     *
     * @param address the IP address string.
     * @return the integer representation of the IP address string.
     */
    private long addressToInt(String address) {
        if (!addressRe.matcher(address).matches()) {
            illegalIpAddressString(address);
        }

        // Convert the address to an integer.
        long result = 0;
        String components[] = address.split("[.]");
        for (int i = 0; i < components.length; i++) {
            int x = Integer.parseInt(components[i]);
            if (x > 255) {
                illegalIpAddressString(address);
            }
            result = (result << 8) + x;
        }
        return result;
    }

    /**
     * Converts the number of significant bits to an IP address mask.
     *
     * @param significantBits the number of significant bits.
     * @return the IP address mask.
     */
    private long significantBitsToMask(int significantBits) {
        long m = 0;
        for (int i = 0; i < 32; i++) {
            int digit = i < significantBits ? 1 : 0;
            m = (m << 1) + digit;
        }
        return m;
    }

    /**
     * Determines whether or not an IP address matches this range.
     *
     * @param address the IP address string.
     * @return true if the address matches the string.
     */
    public boolean matches(String address) {
        return (addressToInt(address) & mask) == (baseAddress & mask);
    }
}

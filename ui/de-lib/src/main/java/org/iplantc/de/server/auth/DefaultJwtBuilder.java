package org.iplantc.de.server.auth;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import static org.iplantc.de.server.auth.PemKeyUtils.loadPrivateKey;
import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;

/**
 * Builds signed JWTs for the current user.
 *
 * @author dennis
 */
@Component
public class DefaultJwtBuilder implements JwtBuilder {

    @Value("${org.iplantc.discoveryenvironment.jwt.private-key-path}") private String privateKeyPath;
    @Value("${org.iplantc.discoveryenvironment.jwt.private-key-password}") private String privateKeyPassword;

    private PrivateKey privateKey;

    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                privateKey = loadPrivateKey(privateKeyPath, privateKeyPassword);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to load JWT signing key", e);
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException("Unable to load JWT signing key", e);
            }
        }
        return privateKey;
    }

    @Override
    public String buildJwt(final HttpServletRequest request) throws JoseException {
        final AttributePrincipal principal = attributePrincipalFromServletRequest(request);

        // Extract the user's first and last name from the attributes.
        final String firstName = getStringAttribute(principal, "firstName");
        final String lastName = getStringAttribute(principal, "lastName");

        // Build the JWT claims.
        JwtClaims claims = new JwtClaims();
        claims.setSubject(principal.getName());
        claims.setClaim("email", getStringAttribute(principal, "email"));
        claims.setClaim("given_name", firstName);
        claims.setClaim("family_name", lastName);
        claims.setClaim("name", firstName + " " + lastName);
        claims.setClaim("org.iplantc.de:entitlement", extractGroups(principal));

        // Sign the key.
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setKey(getPrivateKey());

        return jws.getCompactSerialization();
    }

    /**
     * Obtains the value of a user attribute as a string. Returns null if the attribute is missing
     * or null.
     *
     * @param principal the attribute principal from the incoming request.
     * @param attribute the name of the attribute to extract.
     * @return the attribute as a string or null.
     */
    private String getStringAttribute(final AttributePrincipal principal, final String attribute) {
        final Object value = principal.getAttributes().get(attribute);
        return value == null ? null : value.toString();
    }

    /**
     * Extracts the groups from the user's {@code entitlement} attribute.
     *
     * @param principal the attribute principal from the incoming request.
     * @return the list of groups.
     */
    private String[] extractGroups(final AttributePrincipal principal) {
        final String groupStr = getStringAttribute(principal, "entitlement");
        if (groupStr == null || groupStr.matches("^\\s*$")) {
            return null;
        }

        // Remove the leading and trailing square brackets then split the string.
        return groupStr.replaceAll("^\\s*\\[|\\]\\s*$", "").split(",\\s*");
    }
}

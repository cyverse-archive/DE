package org.iplantc.de.server.auth;

import org.jose4j.lang.JoseException;

import javax.servlet.http.HttpServletRequest;

/**
 * Builds signed JWTs for the current user.
 *
 * @author dennis
 */
public interface JwtBuilder {

    /**
     * Builds a signed JWT for the current user.
     *
     * @param request the incoming HTTP servlet request.
     * @return the signed and encoded JWT.
     * @throws JoseException if the JWT can't be generated or signed.
     */
    String buildJwt(final HttpServletRequest request) throws JoseException;
}

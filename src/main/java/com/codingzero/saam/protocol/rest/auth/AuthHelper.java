package com.codingzero.saam.protocol.rest.auth;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by ruisun on 2017-01-01.
 */
public class AuthHelper {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_QUERY_STRING = "token";

    public static String readToken(ContainerRequestContext requestContext) {
        if (null == requestContext.getHeaderString(AUTH_HEADER)
                && null == requestContext.getProperty(TOKEN_QUERY_STRING)) {
            throw new AuthenticationFailedException(
                    "To perform this action, you need give either "
                            + AUTH_HEADER + " header or "
                            + TOKEN_QUERY_STRING + " query string");
        }
        if (null == requestContext.getHeaderString(AUTH_HEADER)) {
            return requestContext.getHeaderString(AUTH_HEADER);
        } else {
            return (String) requestContext.getProperty(TOKEN_QUERY_STRING);
        }
    }

}

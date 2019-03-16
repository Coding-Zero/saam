package com.codingzero.saam.protocol.rest.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ruisun on 2017-01-01.
 */
public class AuthHelper {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_QUERY_STRING = "token";

    public static String readToken(HttpServletRequest request) {
        if (null == request.getHeader(AUTH_HEADER)
                && null == request.getParameter(TOKEN_QUERY_STRING)) {
            throw new AuthFailedException(
                    "To perform this action, you need give either "
                            + AUTH_HEADER + " header or "
                            + TOKEN_QUERY_STRING + " query string");
        }
        if (null != request.getHeader(AUTH_HEADER)) {
            return request.getHeader(AUTH_HEADER);
        } else {
            return request.getParameter(TOKEN_QUERY_STRING);
        }
    }

}

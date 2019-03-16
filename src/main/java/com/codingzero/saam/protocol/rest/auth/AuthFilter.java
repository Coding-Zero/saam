package com.codingzero.saam.protocol.rest.auth;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.lang.reflect.Method;

@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest servletRequest;

    private AuthHandler handler;

    public AuthFilter(AuthHandler handler) {
        this.handler = handler;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final Method method = resourceInfo.getResourceMethod();
        Auth auth = method.getAnnotation(Auth.class);
        if (null == auth) {
            return;
        }
        String token = AuthHelper.readToken(servletRequest);
        System.out.println("HERE======>token: " + token);
        String authHandlerName = auth.name().length == 0 ? null : auth.name()[0];
        System.out.println("HERE======>authHandlerName: " + authHandlerName);
        if (!handler.verify(new AuthContext(token, authHandlerName))) {
            throw new AuthFailedException(
                    "This action is not authorized to perform!");
        }
    }
}

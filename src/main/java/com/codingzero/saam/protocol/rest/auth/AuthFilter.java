package com.codingzero.saam.protocol.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandlerManager.class);

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
        Auth auth = method.getDeclaringClass().getAnnotation(Auth.class);
        if (null == auth) {
            auth = method.getAnnotation(Auth.class);
        }
        if (null == auth) {
            return;
        }
        String token = AuthHelper.readToken(servletRequest);
        String authHandlerType = auth.value().length == 0 ? null : auth.value()[0];
        LOG.info("You are trying do " + authHandlerType
                + " AUTH check for method " + method.getClass().getCanonicalName());
        if (!handler.verify(new AuthContext(token, authHandlerType))) {
            throw new AuthFailedException(
                    "This action is not authorized to perform!");
        }
    }
}

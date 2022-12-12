package com.tutorial.crud.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtEntryPoint implements AuthenticationEntryPoint { //clase q devuelve respuestas del tipo 401 no autorizado, etc.

    private final static Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
        logger.error("fail en el metodo commence");
        logger.info("no autorizado");
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "no autorizado");
    }


}

package com.tutorial.crud.security.jwt;

import com.tutorial.crud.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//clase que en caso que sea valido el token permite el acceso al recurso
public class JwtTokenFilter extends OncePerRequestFilter {
    private final static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    //con este método spring security valida internamente el token. Primero validamos nosotros con nuestro método personalizado validateToken() y si pasa este primer filtro se va a un segundo filtro interno de spring para lo cual debemos generar una instancia UsernamePasswordAuthenticationToken a partir del token previamente validado con validateToken()
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException { //esto se ejecuta en cada peticion al servidor
        try {
            String token = getToken(req);
            if (token != null && jwtProvider.validateToken(token)) {
                String nombreUsuario = jwtProvider.getNombreUsuarioFromToken(token); //se obtiene el usuario a partir del token
                UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario); //obtenemos la data del usuario a partir del nombre de usuario
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);//al contexto de autenticacion se le asigna el usuario pri medio del auth; esto es para que spring security haga su magia en forma interna para la autenticación
            }
        } catch (Exception e) {
            logger.error("fail en el metodo doFilter");
        }
        filterChain.doFilter(req, res);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer"))
            return header.replace("Bearer", "");
        return null;
    }

}

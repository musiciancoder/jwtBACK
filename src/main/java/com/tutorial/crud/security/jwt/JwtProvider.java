package com.tutorial.crud.security.jwt;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.tutorial.crud.security.dto.JwtDto;
import com.tutorial.crud.security.entity.UsuarioPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider { //clase q genera el token
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}") //esto esta en el archivo properties
    private String secret;
    @Value("${jwt.expiration}")  //esto esta en el archivo properties, es el tiempo de expiracion del token
    private int expiration;

    //método para crear el token. Se ocupa en el método de login del controlador
    public String generateToken(Authentication authentication) {
        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal(); //obtenemos el usuario principal, o sea el usuario con autorizaciones
        List<String> roles = usuarioPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()); //obtenemos los roles a partir de las autorizaciones del usuario principal
        return Jwts.builder() //acá se genera el token
                .setSubject(usuarioPrincipal.getUsername())
                .claim("roles",roles) //acá le decimos q en el payload va a tener que mostrar un campo llamado "roles"
                .setIssuedAt(new Date()) //fecha de creacion
                .setExpiration(new Date(new Date().getTime() + expiration))
                .signWith(SignatureAlgorithm.ES512, secret.getBytes()) //El algoritmo elegido y la firma. Si no le ponemos el getBytes, en el video 14 al probar en jwt.io aparece "signature invalid"
                .compact();
    }

    //al parecer este método hace lo mismo que cuando pegamos un token en el sitio web de jwt y obtenemos los datos del usuario a la derecha
    public String getNombreUsuarioFromToken(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
    }

    //para validar el token en las peticiones, por lo que este metodo se llama en la clase JwtTokenFilter
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token); //la firma
            return true;
        } catch (MalformedJwtException e) {
            logger.error("token mal formado");
        } catch (UnsupportedJwtException e) {
            logger.error("token no soportado");
        } catch (ExpiredJwtException e) {
            logger.error("token expirado");
        } catch (IllegalArgumentException e) {
            logger.error("token vacio");
        } catch (SignatureException e) {
            logger.error("fail en la firma");
        }
        return false;
    }

    //Esto es para crear otro token que sea valido ya que el token anterior expira luego de 20 seg
    public String refreshToken (JwtDto jwtDto) throws ParseException {
        try{
            //si no esta expirado el token sencillamente nos metemos con la firma (video 32)
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtDto.getToken()); //la firma
        }catch (ExpiredJwtException e) {
            //Esto es si esta expirado el token
            JWT jwt = JWTParser.parse(jwtDto.getToken());
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String nombreUsuario = claims.getSubject();
            List<String> roles = (List<String>) claims.getClaims("roles");

            return Jwts.builder().setSubject(nombreUsuario)
                    .claim("roles", roles) //acá le decimos q en el payload va a tener que mostrar un campo llamado "roles"
                    .setIssuedAt(new Date()) //fecha de creacion
                    .setExpiration(new Date(new Date().getTime() + expiration))
                    .signWith(SignatureAlgorithm.ES512, secret.getBytes()) //si no le ponemos el getBytes, en el video 14 al probar en jwt.io aparece "signature invalid"
                    .compact();
        }
        return null;
    }

}




package com.tutorial.crud.security.dto;

//clase para compartir el token al usuario
public class JwtDto {
    private String token;

    //No es tan necesario

    public JwtDto() {
    }

    //Ojo!! En un principio esta clase llevaba el token, nombreUsuario y authorities, pero en el video 13 lo modificó solo para que mostrara el token por posible vulnerabilidad, ya que en Herramientas-->Application se podian modificar las autorities
    public JwtDto(String token){
        this.token = token;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
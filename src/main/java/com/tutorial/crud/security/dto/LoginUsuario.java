package com.tutorial.crud.security.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class LoginUsuario { //clase para hacer login de un usuario ya registrado en la bbdd
    @NotBlank
    private String nombreUsuario;
    @NotBlank
    private String password;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

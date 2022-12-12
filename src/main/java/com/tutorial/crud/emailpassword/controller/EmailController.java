package com.tutorial.crud.emailpassword.controller;

import com.tutorial.crud.dto.Mensaje;
import com.tutorial.crud.emailpassword.dto.ChangePasswordDTO;
import com.tutorial.crud.emailpassword.dto.EmailValuesDTO;
import com.tutorial.crud.emailpassword.service.EmailService;
import com.tutorial.crud.security.entity.Usuario;
import com.tutorial.crud.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/email-password")
@CrossOrigin //para que las politicas de CORS no bloqueen las peticiones desde el front
public class EmailController {

    @Autowired
    EmailService emailService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${mail.subject}")
    private String subject;

    //Con plantilla thymeleaf
    @PostMapping("/send-email") //esto es para enviar un correo para recuperar la contraseña
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioService.getByNombreUsuarioOrEmail(dto.getMailTo()); //recuperar usuario por  email
        if (!usuarioOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("No existe ningun usuario con esas credenciales"), HttpStatus.NOT_FOUND);
        }
        Usuario usuario = usuarioOpt.get();
        //en este punto en el dto solo tenemos el email (que fue el q escribió el tipo para recuperar credenciales. En las siguientes lineas se llenan el resto de los atributos del dto)
        dto.setMailFrom(mailFrom);
        dto.setMailTo(usuario.getEmail());
        dto.setSubject(subject);
        dto.setUserName(usuario.getNombreUsuario());
        UUID uuid = UUID.randomUUID(); //te genera una cadena cualquiera para poder porbar
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        usuario.setTokenPassword(tokenPassword);
        usuarioService.save(usuario);
        emailService.sendEmail(dto);
        return new ResponseEntity(new Mensaje("Te hemos enviado un correo"), HttpStatus.OK);
    }

    //Aca el usuario cambia la contraseña. Es independiente del mail de recuperacion (no es secuencial, es decir no es que vaya el mail primero y cambie contraseña despues)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(new Mensaje("Campos mal escritos"), HttpStatus.BAD_REQUEST);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return new ResponseEntity(new Mensaje("Las contraseñas no coinciden"), HttpStatus.OK);
        } //password y confirm password no son las mismas
        Optional<Usuario> usuarioOpt = usuarioService.getByTokenPassword(dto.getTokenPassword());
        if (!usuarioOpt.isPresent()) {

                return new ResponseEntity(new Mensaje("No existe ningun usuario con esas credenciales"), HttpStatus.NOT_FOUND);
            } //esto es porque el tokenPassword no es valido
            Usuario usuario = usuarioOpt.get();
            String newPassword = passwordEncoder.encode(dto.getPassword());
            usuario.setPassword(newPassword);
            usuario.setTokenPassword(null);
            usuarioService.save(usuario);
            return new ResponseEntity(new Mensaje("Contraseña actualizada"), HttpStatus.OK);
        }



}

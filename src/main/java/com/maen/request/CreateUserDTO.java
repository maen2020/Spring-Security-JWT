package com.maen.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Dentro de este DTO se va a implementar lo mismo que se tiene en UserEntity
 */
@Data //Generar los Getter y Setter.
@AllArgsConstructor //Generar un constructor con parametros.
@NoArgsConstructor //Generar un constructor vacio.
public class CreateUserDTO {

    @Email //Validaciones.
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    //Variable para poder obtener los roles desde la peticion.
    private Set<String> roles;
}

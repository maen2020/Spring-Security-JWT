package com.maen.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Generar metodos Get y Set.
@AllArgsConstructor //Constructor con parametros.
@NoArgsConstructor //Constructor vacio.
@Builder //Construir objetos de una clase.
@Entity
@Table(name = "users") //Nombre del tabla de la db para el mapeo.
public class UserEntity {

    //Atributos de la clase.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email()
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;
}
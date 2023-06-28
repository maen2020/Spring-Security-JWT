package com.maen.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Generar metodos Get y Set.
@AllArgsConstructor //Constructor con parametros.
@NoArgsConstructor //Constructor vacio.
@Builder //Construir objetos de la clase.
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole name;
}

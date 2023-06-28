package com.maen.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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

    /**
     * Crear relacion entre la tabla UserEntity y ERole.
     * List: Permite tener varios ADMIN
     * Set: Solo un ADMIN
     * Eager: Traer todos los roles ligados a ese usuario.
     * Lazy: Solo uno por uno cada vez que se solicite.
     * targetEntity: Con que entidad se va a establcer la relacion.
     * Cascade: Pesistir solo el rol aunque se elimine el usurio.
     */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)

    /**
     * Configuracion de claves foraneas.
     * Cuando hay una relacion de ManyToMany se debe configurar una tabla intermedia.
     * Configurar como se van a llamar las claves foraneas.
     * joinColumns: Confiurar nombre de las claves foraneas (usuarios y roles).
     */
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
}
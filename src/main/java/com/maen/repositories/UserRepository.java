package com.maen.repositories;

import com.maen.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Metodo para hacer la busqueda de un usuario por el nombre de usuario.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Metodo personalizado para hacer la busqueda de un usuario por el nombre de usuario.
     */
    @Query("select u from UserEntity u where u.username = ?1")
    Optional<UserEntity> getName(String username);
}

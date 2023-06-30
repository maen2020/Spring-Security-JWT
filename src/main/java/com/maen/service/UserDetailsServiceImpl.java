package com.maen.service;

import com.maen.models.UserEntity;
import com.maen.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Clase personalizada de UserDetailsService para recuperar los usuarios desde la base de datos con sus permisos y roles.
 */
@Service //Decirle a spring que va hacer un objeto administrado por spring.
public class UserDetailsServiceImpl implements UserDetailsService {

    //Inyectar clase para consultar el usuario a la base de datos.
    @Autowired
    private UserRepository userRepository;

    /**
     * Clase que spring consulta por debajo en su core para asegurarse cual va hacer el usuario que se va a consultar.
     * Clase para decirle a spring de donde va a traer los usuarios.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * Recuperando el usuario de la base de datos.
         */
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe.")); //Retorna un lamda para decir que no existe el usuario.

        /**
         * Creando la autorizacion que necesita spring security.
         * Obtener los permisos del usuario.
         */
        Collection<? extends GrantedAuthority> authorities = userEntity.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())))
                .collect(Collectors.toSet()); //Convertir a una lista.

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}

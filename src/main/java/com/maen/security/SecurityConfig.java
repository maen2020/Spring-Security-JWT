package com.maen.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Configuracion de spring.
public class SecurityConfig {

    /**
     * Metodo para hacer la configuracion de toda la seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(config -> config.disable()) //Deshabilitarlo cuando no se trabaja con formularios.

                // Configurar cuales URL's van a estar protegidas o permitidas.
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll(); //Arreglo de strings de urls sin autorizacion.
                    auth.anyRequest().authenticated(); //Cualquier otra ruta o endpoint necesita autenticacion.
                })

                //Administracion de la sesion.
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Definir la politica de la creacion de la sesion.
                })
                .httpBasic() //Hacer una autenticacion basica, esta se hace con usuario en memoria.
                .and()
                .build();
    }

    /**
     * #1
     * Creacion de un usuario en memory con accesos (OPTIONAL).
     * Solo si no se cuenta con una base de datos.
     */
    @Bean
    UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("Miguel") //Agregando valores al usuario en memoria.
                .password("1234")
                .roles()
                .build());

        return manager;
    }

    /**
     * #2
     * Objeto que se encarga de la administracion de la autenticacion de los usuarios.
     * Este metodo requiere un passwordEncoder para encriptar las contraseñas.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService()) //Enviar el usuario que se va a autenticar.
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    /**
     * #3
     * Se crea para poder insertarlo en metodo AuthenticationManager.
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}

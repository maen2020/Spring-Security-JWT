package com.maen.security;

import com.maen.security.filters.JwtAuthenticationFilter;
import com.maen.security.jwt.JwtUtils;
import com.maen.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Configuracion de spring.
public class SecurityConfig {

    //Inyectar la clase JwtUtil que va a recibir el filtro de autenticacion.
    @Autowired
    private JwtUtils jwtUtils;

    //Inyectar la clase para traer los usuarios de la db.
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Metodo para hacer la configuracion de toda la seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils); //Definir el filtro de autenticacion.
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager); //Setear un authenticationManager al jwtAuthenticationFilter.
        jwtAuthenticationFilter.setFilterProcessesUrl("/login"); //Setear otra ruta si no se quiere el login que viene por defecto.

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
                .addFilter(jwtAuthenticationFilter)
                .build();
    }

    /**
     * #2
     * Objeto que se encarga de la administracion de la autenticacion de los usuarios.
     * Este metodo requiere un passwordEncoder para encriptar las contraseñas.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService) //Enviar el usuario que se va a autenticar.
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    /**
     * #3
     * Se crea para poder insertarlo en el metodo AuthenticationManager.
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //Definiendo la politica de encriptacion.
    }
}

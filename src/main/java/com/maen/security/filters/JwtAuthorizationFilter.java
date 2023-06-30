package com.maen.security.filters;

import com.maen.security.jwt.JwtUtils;
import com.maen.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OncePerRequest: Se va a autenticar una vez por cada peticion a los enpoints.
 * Clase para autenticarse validando correctamente el token.
 * Se puede definir como un component dado a que no se le va a enviar un parametro adicional.
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    //Inyectar la clase para validar el token.
    @Autowired
    private JwtUtils jwtUtils;

    //Inyectar la clase para consultar el usuario en la base de datos.
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Extraer el token de la peticion.
        String tokenHeader = request.getHeader("Authorization");
        //Validar el token
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            String token = tokenHeader.substring(7);

            //Validar si el token es valido.
            if (jwtUtils.isTokenValid(token)){
                String username = jwtUtils.getUsernameFromToken(token); //Recuperar el usuario que viene denyro del token.
                //Recuperar los detalles del usuario(usuario, contraseña y roles).
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //Autenticarse.
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken); //Contiene la autenticacion propia de la aplicacion.
            }
        }

        /**
         * Continuar con el filtro de validacion.
         * Denegar el acceso.
         */
        filterChain.doFilter(request, response);
    }
}

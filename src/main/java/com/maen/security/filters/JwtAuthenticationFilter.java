package com.maen.security.filters;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maen.models.UserEntity;
import com.maen.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que maneja el filtro de autenticacion.
 * Cuando un usuario se valla a registrar.
 * No se define como componente dado a que se le envia varios argumentos y setear varios atributos.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //Inyectar la clase JwtUtils por constructor para generar el token.
    private JwtUtils jwtUtils;
    public JwtAuthenticationFilter(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    /**
     * Metodo para cuando intentan autenticarse en la aplicacion.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        /**
         * Recuperar el usuario que ha intentado autenticarse.
         * Con esto se obtiene el username del usuario.
         */
        UserEntity userEntity = null;
        String username = "";
        String password = "";
        try {
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            username = userEntity.getUsername(); //Obteniendo el usuario y la contraseña.
            password = userEntity.getPassword();
        } catch (StreamReadException e){
            throw  new RuntimeException(e);
        } catch (DatabindException e){
            throw new RuntimeException(e);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password); //Con este autenticationToken se va a autenticar en la aplicacion.

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * Metodo para cuando se ha autenticado correctamente.
     * Dentro de este metodo se genera el token cuando se autentica correctamente.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        /**
         * Generar el token obteniendo los detalles del usuario (usuario, contraseña y roles).
         *
         */
        User user = (User) authResult.getPrincipal(); //Se obtiene el objeto que contiene los detalles del usuario.

        /**
         * Generar el token de acceso para dar autorizacion de acceso a los endpoints.
         * Inyectar la clase JwtUtils para utilizar la creacion del token.
         */
        String token = jwtUtils.generateAccessToken(user.getUsername());

        /**
         * Responder a la solicitud del login con el token de acceso.
         */
        response.addHeader("Authorization", token); //En el header de la respuesta se va a enviar el token.

        /**
         * Responder el token en el cuerpo de la respuesta.
         */
        Map<String, Object> httpResponse = new HashMap<>(); //Con este Map se va a mapear la respuesta y a convertir en un json.
        httpResponse.put("Token: ", token);
        httpResponse.put("Message: ", "Autenticacion Correcta.");
        httpResponse.put("Username: ", user.getUsername());

        /**
         * Escribir el Map como un json en la respuesta.
         */
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value()); //Otro parametro en la respuesta.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush(); //Garantizar quetodo se escriba correctamenete.
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
package com.maen.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Clase que proveer los metodos necesarios para trabajar con el Token.
 */
@Component //Componente administrado por spring.
@Slf4j //Implementa logs.
public class JwtUtils {

    //Atributos de la clase para generar el token.
    @Value("${jwt.secret.key}") //Parametro traido del application.properties.
    private String secretKey; //Ayuda a firmar el token.

    @Value("${jwt.time.expiration}") //Parametro traido del application.properties.
    private String timeExpiration; //Tiempo de validez del token.

    /**
     * Metodo el cual se va a encargar de generar el token de acceso.
     * Para generarlo se necesita el username
     */
    public String generateAccessToken(String username){
        return Jwts.builder()
                .setSubject(username) //Enviar el usurio que va a generar el token.
                .setIssuedAt(new Date(System.currentTimeMillis())) //Fecha de creacion del token.
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration))) //Fecha de expiracion del token, convertir de String a Long.
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256) //Firma del metodo y algoritmo de encriptacion.
                .compact();
    }

    /**
     * #1
     * Obtener firma del token.
     */
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //Decodificar la clave y volverla a encriptar en un algoritmo de encriptacion que sirva para firmar el token.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * #2
     * Metodo que se encarga de validar que el token de acceso sea correcto.
     */
    public boolean isTokenValid(String token){
        try {
            Jwts.parserBuilder() //Lee el token.
                    .setSigningKey(getSignatureKey()) //Enviar la firma.
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e){
            log.error("Token invalido, error: ".concat(e.getMessage())); //Imprimir el mensaje de error en la consola.
            return false;
        }
    }

    /**
     * #3
     * Metodo para obtener las caracteristicas del token.
     * Obtener los claims osea la informacion que viene dentro del token.
     */
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder() //Lee el token.
                .setSigningKey(getSignatureKey()) //Enviar el token.
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * #4
     * Obtener un solo claim osea un solo campo de informacion del token.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){ //Recibe el token, el function recibe todos los claims y va a retornar el T que es un generico.
        Claims claims = extractAllClaims(token); //Obtener la lista de los claims, para poder obtener uno solo.
        return claimsTFunction.apply(claims);
    }

    /**
     * #5
     * Obtener el username (usuario) que viene dentro del token.
     */
    public String getUsernameFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }
}
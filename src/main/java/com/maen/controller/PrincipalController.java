package com.maen.controller;

import com.maen.models.ERole;
import com.maen.models.RoleEntity;
import com.maen.models.UserEntity;
import com.maen.repositories.RoleRepository;
import com.maen.repositories.UserRepository;
import com.maen.request.CreateUserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PrincipalController {

    //Inyectar la clase para poder persistir (guardar) el usuario creado en la db.
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World Not Secured";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World Secured";
    }

    /**
     * Valid: Validacion a los campos.
     * RequestBody: Enviar el cuerpo por la request(solicitud) para ser guardada.
     */
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){

        //Recuperar los roles y convertirlo a un SetRoleEntity para poder insertarlo en la db.
        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet());

        //Creacion del usuario en la db.
        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(createUserDTO.getPassword())
                .email(createUserDTO.getEmail())
                .roles(roles)
                .build();

        //Guardar el usuario creado en la db.
        userRepository.save(userEntity);

        //Retornar el usuario creado.
        return ResponseEntity.ok(userEntity);
    }

    /**
     * RequestParam: Enviar el id el cual sera eliminado.
     * El id es de tipo Long y tiene que ser enviado como string por lo tanto se tiene que convertir.
     */
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));
        return "Se ha borrado el user cn id".concat(id);
    }
}

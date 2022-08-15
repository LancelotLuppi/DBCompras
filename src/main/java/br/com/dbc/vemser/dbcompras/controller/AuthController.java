package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.*;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.security.TokenService;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@Controller
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> auth(@RequestBody @Valid LoginDTO login){
        return ResponseEntity.ok(usuarioService.validarLogin(login));
    }

    @GetMapping("/get-islogged")
    public ResponseEntity<UsuarioLoginDTO> getUser () throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PostMapping("/create-user")
    public ResponseEntity<UsuarioReturnDTO> create(@RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        return ResponseEntity.ok(usuarioService.create(usuarioCreateDTO));
    }

    @PutMapping("/update-credenciais")
    public ResponseEntity<UsuarioUpdateLoginDTO> update
            (@RequestBody UsuarioUpdateLoginDTO usuarioUpdateLoginDTO
            ) throws UsuarioException {
        return ResponseEntity.ok(usuarioService.updatePassword(usuarioUpdateLoginDTO));
    }

}

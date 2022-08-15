package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.*;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.CargoUsuario;
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

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<String> auth(@RequestBody @Valid LoginDTO login){

        UsernamePasswordAuthenticationToken userPassAuthToken =
                new UsernamePasswordAuthenticationToken(
                        login.getLogin(),
                        login.getSenha());

        Authentication authentication =
                authenticationManager.authenticate(userPassAuthToken);

        Object usuarioLogado =  authentication.getPrincipal();
        UsuarioEntity usuarioEntity = (UsuarioEntity) usuarioLogado;

        return ResponseEntity.ok(tokenService.generateToken(usuarioEntity));
    }

    @GetMapping("/get-islogged")
    public ResponseEntity<UsuarioLoginDTO> getUser () throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PostMapping("/create-user")
    public ResponseEntity<UsuarioDTO> create(
            @RequestBody UsuarioCreateDTO usuarioCreateDTO,
            CargoUsuario cargos) throws JsonProcessingException {
        return ResponseEntity.ok(usuarioService.create(usuarioCreateDTO, cargos));
    }

    @PutMapping("/update-credenciais")
    public ResponseEntity<UsuarioUpdateLoginDTO> update
            (@RequestBody UsuarioUpdateLoginDTO usuarioUpdateLoginDTO
            ) throws UsuarioException {
        return ResponseEntity.ok(usuarioService.updateLogin(usuarioUpdateLoginDTO));
    }

}

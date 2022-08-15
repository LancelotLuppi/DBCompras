package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.LoginDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginReturnDTO;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/get-logged")
    public ResponseEntity<LoginReturnDTO> getUser () throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PostMapping("/create-user")
    public ResponseEntity<LoginReturnDTO> create(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(usuarioService.create(loginDTO));
    }

    @PutMapping("/update-credenciais")
    public ResponseEntity<LoginDTO> update
            (@RequestBody LoginDTO usuarioUpdateLoginDTO
            ) throws UsuarioException {
        return ResponseEntity.ok(usuarioService.updatePassword(usuarioUpdateLoginDTO));
    }

}

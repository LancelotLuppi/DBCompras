package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.documentation.AuthDocumentation;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginDTO;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Controller
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthDocumentation {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> auth(@Valid @RequestBody UserLoginDTO login) throws RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.validarLogin(login));
    }
}

package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.LoginAccessDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginUpdateDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusUsuario;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;


    @PostMapping("/create-user")
    public ResponseEntity<LoginDTO> create(@RequestBody LoginCreateDTO loginCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.create(loginCreateDTO));
    }

    @GetMapping("/get-logged")
    public ResponseEntity<LoginDTO> getUser () throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PutMapping("/logged-user/nome-e-email")
    public ResponseEntity<LoginDTO> updateLogged(@Valid @RequestBody LoginUpdateDTO usuario)
            throws UsuarioException {
        return ResponseEntity.ok(usuarioService.updateLoggedUser(usuario));
    }

    @PutMapping("/logged-user/change-password")
    public ResponseEntity<LoginDTO> updatePassword(@RequestBody String novaSenha) throws UsuarioException {
        return ResponseEntity.ok(usuarioService.updatePassword(novaSenha));
    }

    @PutMapping("/logged-user/status-account")
    public void updateStatusLoggedAccount(LoginAccessDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        usuarioService.desativarContaLogada(confirmacao);
    }
}

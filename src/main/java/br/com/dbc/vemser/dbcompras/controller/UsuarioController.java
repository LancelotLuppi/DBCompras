package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.*;
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
    public ResponseEntity<UserLoginComSucessoDTO> create(@Valid @RequestBody UserCreateDTO userCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.create(userCreateDTO));
    }

    @GetMapping("/get-logged-user")
    public ResponseEntity<UserDTO> getUser() throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PutMapping("/logged-user")
    public ResponseEntity<UserDTO> updateLogged(@Valid @RequestBody UserUpdateDTO usuario)
            throws UsuarioException, RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.updateLoggedUser(usuario));
    }

    @PutMapping("/change-password")
    public ResponseEntity<UserDTO> updatePassword(@Valid @RequestBody UserUpdatePasswordDTO updatePasswordDTO) throws UsuarioException, RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.updateLoggedPassword(updatePasswordDTO));
    }

    @PutMapping("/logged-user/status-account")
    public void updateStatusLoggedAccount(UserLoginDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        usuarioService.desativarContaLogada(confirmacao);
    }

    @DeleteMapping("/delete/{idUsuario}")
    public void deletarUser(@PathVariable("idUsuario") Integer idUsuario) throws RegraDeNegocioException {
        usuarioService.deletarUsuario(idUsuario);
    }
}

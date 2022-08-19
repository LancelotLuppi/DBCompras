package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.documentation.UsuarioDocumentation;
import br.com.dbc.vemser.dbcompras.dto.usuario.*;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController implements UsuarioDocumentation {

    private final UsuarioService usuarioService;

    @PostMapping("/create-user")
    public ResponseEntity<UserLoginComSucessoDTO> create(@RequestBody @Valid UserCreateDTO userCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(userCreateDTO));
    }

    @GetMapping("/get-logged")
    public ResponseEntity<UserWithCargoDTO> getUser() throws UsuarioException {
        return ResponseEntity.ok(usuarioService.getLoggedUser());
    }

    @PutMapping("/logged-user")
    public ResponseEntity<UserDTO> updateLogged(@Valid @RequestBody UserUpdateDTO usuario)
            throws UsuarioException, RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.updateLoggedUser(usuario));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UserUpdatePasswordDTO updatePasswordDTO) throws UsuarioException, RegraDeNegocioException {
        usuarioService.updateLoggedPassword(updatePasswordDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/logged-user/status-account")
    public ResponseEntity<Void> updateStatusLoggedAccount(UserLoginDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        usuarioService.desativarContaLogada(confirmacao);
        return ResponseEntity.noContent().build();
    }
}

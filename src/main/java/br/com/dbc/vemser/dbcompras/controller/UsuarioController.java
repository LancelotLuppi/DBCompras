package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.UsuarioDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UsuarioLoginDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UsuarioUpdateDTO;
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

    @GetMapping("/find-by-id")
    public ResponseEntity<UsuarioDTO> findById() throws UsuarioException {
        return ResponseEntity.ok(usuarioService.findById());
    }

    @PutMapping("/update-user")
    public ResponseEntity<UsuarioDTO> update(@Valid @RequestBody UsuarioUpdateDTO usuario)
            throws UsuarioException {
        return ResponseEntity.ok(usuarioService.update(usuario));
    }

    @DeleteMapping("/delete-user")
    public void delete() throws UsuarioException {
        usuarioService.delete();
    }
}

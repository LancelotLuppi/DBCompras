package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.UsuarioDTO;
import br.com.dbc.vemser.dbcompras.dto.UsuarioUpdateDTO;
import br.com.dbc.vemser.dbcompras.enums.CargoUsuario;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/find-by-id")
    public ResponseEntity<UsuarioDTO> findById()
            throws UsuarioException {
        return ResponseEntity.ok(usuarioService.findById());
    }

    @PutMapping("/update-user")
    public ResponseEntity<UsuarioDTO> update(
            @RequestParam(required = false) CargoUsuario cargos,
            @Valid @RequestBody UsuarioUpdateDTO usuario
    )
            throws UsuarioException, JsonProcessingException {
        return ResponseEntity.ok(usuarioService.update(usuario, cargos));
    }

    @DeleteMapping("/delete-user")
    public void delete() throws UsuarioException {
        usuarioService.delete();
    }
}

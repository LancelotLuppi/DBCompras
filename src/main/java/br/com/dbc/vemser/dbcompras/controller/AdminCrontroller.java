package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserUpdateByAdminDTO;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminCrontroller {


    private final UsuarioService usuarioService;

    @PutMapping("/usuario/{id}/cargos")
    public UserUpdateByAdminDTO updateUserByAdmin (@PathVariable("id") Integer idUser,
                                                   @RequestParam(value = "cargos") Set<TipoCargo> tipoCargo) throws RegraDeNegocioException {
        return usuarioService.updateUserByAdmin(idUser,tipoCargo);
    }




}

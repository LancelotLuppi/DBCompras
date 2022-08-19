package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserUpdateByAdminDTO;
import br.com.dbc.vemser.dbcompras.enums.ControlarAcesso;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminCrontroller {


    private final UsuarioService usuarioService;

    @PutMapping("/usuario/{idUsuario}/cargos")
    public UserUpdateByAdminDTO updateUserByAdmin (@PathVariable("idUsuario") Integer idUser,
                                                   @RequestParam(value = "cargos") Set<TipoCargo> tipoCargo) throws RegraDeNegocioException {
        return usuarioService.updateUserByAdmin(idUser,tipoCargo);
    }

    @PostMapping("/usuario-criar/cargos")
    public UserDTO criarUserByAdmin (@RequestBody UserCreateDTO userCreateDTO,
                                     @RequestParam(value = "cargos") Set<TipoCargo> tipoCargo) throws RegraDeNegocioException {
        return usuarioService.createUserByAdmin(userCreateDTO, tipoCargo );
    }

    @GetMapping("/list")
    public List<UserDTO> list () {
        return usuarioService.list();
    }

    @DeleteMapping("/controlar-acesso-usuario/{idUsuario}")
    public void controlarAcessoUsuario (@PathVariable Integer idUsuario, ControlarAcesso controlarAcesso) throws RegraDeNegocioException {
        usuarioService.controlarAcessoUsuario(idUsuario, controlarAcesso);
    }

}

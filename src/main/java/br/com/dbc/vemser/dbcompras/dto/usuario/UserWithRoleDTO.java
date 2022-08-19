package br.com.dbc.vemser.dbcompras.dto.usuario;

import br.com.dbc.vemser.dbcompras.dto.cargo.CargoDTO;
import lombok.Data;

import java.util.Set;

@Data
public class UserWithRoleDTO {
    private Integer idUsuario;
    private String nome;
    private Set<CargoDTO> cargos;

}

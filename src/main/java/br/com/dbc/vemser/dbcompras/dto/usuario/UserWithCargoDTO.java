package br.com.dbc.vemser.dbcompras.dto.usuario;

import br.com.dbc.vemser.dbcompras.dto.cargo.CargoDTO;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
public class UserWithCargoDTO {

    @Schema(description = "Identificador único do usuário")
    private Integer idUser;

    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    private String nome;

    @Schema(description = "Email de login do usuário", example = "meuteste@teste.com")
    private String email;

    @Schema(description = "Cargos do usuário", example = "COLABORADOR")
    private List<CargoDTO> cargos;

    private Optional<String> imagemPerfilB64;

}

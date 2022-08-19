package br.com.dbc.vemser.dbcompras.dto.usuario;

import br.com.dbc.vemser.dbcompras.dto.cargo.CargoDTO;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Data
public class UserCreateByAdminDTO {

    @Schema(description = "Identificador único do usuário")
    private Integer idUser;

    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    @NotEmpty
    private String nome;

    @Schema(description = "Email de login do usuário", example = "meuemail@dbccompany.com.br")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Senha do usuário", example = "Ab123456@")
    @NotBlank
    private String senha;

    @Schema(description = "Foto do usuário", example = "Base64 da img")
    private Optional<String> foto;

    private CargoDTO cargoDTO;

}

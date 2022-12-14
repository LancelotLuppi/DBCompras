package br.com.dbc.vemser.dbcompras.dto.usuario;

import br.com.dbc.vemser.dbcompras.dto.cargo.CargoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class UserUpdateByAdminDTO {

    @Schema(description = "Identificador único do usuário")
    private Integer idUser;

    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    @NotBlank
    private String nome;

    @Schema(description = "Email de login do usuário", example = "meuemail@dbccompany.com.br")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Foto do usuário", example = "Base64 da img")
    private String foto;

    @NotNull
    private CargoDTO tipoCargo;


}

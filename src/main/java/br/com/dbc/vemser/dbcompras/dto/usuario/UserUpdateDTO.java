package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDTO {

    @NotBlank
    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    private String nome;

    @NotBlank
    @Schema(description = "Email de login do usuário", example = "meuemail@dbccompany.com.br")
    private String email;

    @Schema(description = "Foto do usuário")
    private String foto;

}

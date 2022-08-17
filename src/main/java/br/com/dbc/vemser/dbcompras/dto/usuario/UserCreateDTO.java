package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserCreateDTO {

    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    @NotBlank
    private String nome;

    @Schema(description = "Email de login do usuário", example = "meuemail@dbccompany.com.br")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Senha do usuário", example = "Ab123456@")
    @NotBlank
    private String senha;

    @Schema(description = "Foto do usuário", example = "Base64 da img")
    private String foto;

}

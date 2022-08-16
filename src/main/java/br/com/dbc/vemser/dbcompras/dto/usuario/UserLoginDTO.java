package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {

    @Schema(description = "Email de login do usuário", example = "meuemail@dbccompany.com.br")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Senha do usuário", example = "123")
    @NotBlank
    private String password;

}

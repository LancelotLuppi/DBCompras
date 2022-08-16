package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginAccessDTO {
    @NotBlank
    @Schema(description = "Email de login do usuário", example = "meuteste@teste.com")
    private String email;

    @Email
    @NotBlank
    @Schema(example = "123")
    private String password;
}

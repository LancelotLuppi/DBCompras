package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginDTO {

    @Hidden
    private Integer idUser;

    @NotBlank
    @Schema(description = "Nome completo do usuario", example = "Gabriel Luppi")
    private String nome;

    @NotEmpty
    @NotNull
    @Email
    @Schema(example = "meuteste@dominio.com.br")
    private String email;

    @NotEmpty
    @NotNull
    @Schema(example = "123")
    private String password;

    private String imagemPerfilB64;
}

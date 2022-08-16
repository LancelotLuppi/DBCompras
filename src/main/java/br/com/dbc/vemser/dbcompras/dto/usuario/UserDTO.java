package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {

    @Schema(description = "Identificador único do usuário")
    private Integer idUser;

    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    private String nome;

    @Schema(description = "Email de login do usuário", example = "meuteste@teste.com")
    private String email;

}

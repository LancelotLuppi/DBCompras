package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginDTO {

    @Hidden
    private Integer idUser;

    @NotEmpty
    @Schema(example = "meuteste@dominio.com.br")
    private String email;

    @NotEmpty
    @Schema(example = "123")
    private String password;
}

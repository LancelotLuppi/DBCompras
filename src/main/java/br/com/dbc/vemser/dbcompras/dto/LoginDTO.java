package br.com.dbc.vemser.dbcompras.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginDTO {

    @Schema(example = "Email de cadastro")
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}

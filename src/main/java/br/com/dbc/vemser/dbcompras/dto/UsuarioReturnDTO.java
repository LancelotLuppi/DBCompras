package br.com.dbc.vemser.dbcompras.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UsuarioReturnDTO {
    @Schema(example = "Juliana da Silva", description = "nome completo do usuario")
    @NotNull
    @NotEmpty
    private String nome;

    @Schema(example = "seuEmail@dominio.com", description = "email do usuario")
    @NotEmpty
    private String email;
}

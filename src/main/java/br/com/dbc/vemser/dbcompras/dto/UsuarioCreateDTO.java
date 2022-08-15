package br.com.dbc.vemser.dbcompras.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {

    @Schema(example = "Juliana da Silva", description = "nome completo do usuario")
    @NotEmpty
    private String nomeCompleto;

    @Schema(description = "login do usuario", example = "Juliana")
    @NotEmpty
    private String login;

    @Schema(description = "senha do usuario", example = "Drag@nBorn1")
    @NotEmpty
    private String senha;

    @Schema(example = "faker@faker.com", description = "email do usuario")
    @NotEmpty
    private String email;


}

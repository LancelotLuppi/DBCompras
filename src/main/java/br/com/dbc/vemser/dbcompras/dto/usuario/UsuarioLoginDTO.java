package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginDTO {

    @Schema(description = "login do usuario", example = "joão")
    @NotBlank
    private String email;

    @Schema(example = "Drag@nBorn1")
    @NotBlank
    private String password;

}

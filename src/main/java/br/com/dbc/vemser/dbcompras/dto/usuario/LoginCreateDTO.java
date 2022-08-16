package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
public class LoginCreateDTO extends LoginAccessDTO {
    @NotEmpty
    @Schema(description = "Nome completo do usuário", example = "Gabriel Luppi")
    private String nome;

    @Schema(description = "String em Base64 decodificada da imagem de perfil do usuário")
    private String imagemPerfilB64;
}

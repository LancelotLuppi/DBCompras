package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Data
public class UserWithProfileImageDTO extends UserDTO {

    @Schema(description = "String em Base64 decodificada da imagem de perfil do usuário")
    private Optional<String> imagemPerfilB64;

}

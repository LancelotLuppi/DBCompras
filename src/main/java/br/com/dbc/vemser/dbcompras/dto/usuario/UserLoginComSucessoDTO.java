package br.com.dbc.vemser.dbcompras.dto.usuario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Data
@JsonIgnoreProperties
public class UserLoginComSucessoDTO extends UserDTO {

    @Schema(description = "Token de acesso do usuário")
    private String token;

    @Schema(description = "Foto do usuário")
    private Optional<String> imagemPerfilB64;

}

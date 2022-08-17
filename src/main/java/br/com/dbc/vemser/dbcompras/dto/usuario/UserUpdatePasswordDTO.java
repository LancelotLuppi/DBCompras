package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserUpdatePasswordDTO {
    @Schema(description = "Senha atual do usuário para confirmação da troca de senha")
    private String senhaAtutal;
    @Schema(description = "Nova senha que o usuário deseja colocar")
    private String novaSenha;
}

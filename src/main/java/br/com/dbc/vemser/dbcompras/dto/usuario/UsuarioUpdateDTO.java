package br.com.dbc.vemser.dbcompras.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UsuarioUpdateDTO {

    @Schema(example = "Juliana", description = "Nome do usuario")
    private String nome;

    @Schema(example = "faker@faker.com", description = "Email do usuario")
    private String email;

}

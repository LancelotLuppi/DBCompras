package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.enums.CargoUsuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {

    private Integer idUser;

    private String nome;

    private String email;

    private String password;

    private byte photo;

    private boolean enable;

    private CargoUsuario cargoUsuario;

}

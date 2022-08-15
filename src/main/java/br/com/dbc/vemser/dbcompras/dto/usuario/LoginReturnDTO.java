package br.com.dbc.vemser.dbcompras.dto.usuario;

import lombok.Data;

@Data
public class LoginReturnDTO {
    private Integer idUser;
    private String nome;
    private String email;
}

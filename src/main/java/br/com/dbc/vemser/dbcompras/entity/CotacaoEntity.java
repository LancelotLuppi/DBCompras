package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CotacaoEntity {

    private Integer idCotacao;

    private CompraEntity compras;

    private UsuarioEntity usuario;

    private String nome;

    private LocalDateTime localDate;

    private String anexo;

    private StatusCotacoes status;

}

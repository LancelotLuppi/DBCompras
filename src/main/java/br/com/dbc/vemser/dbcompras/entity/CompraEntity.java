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
public class CompraEntity {

    private Integer idCompra;

    private UsuarioEntity usuario;

    private LocalDateTime dataCompra;

    private StatusCotacoes status;

    private Double valor;

}

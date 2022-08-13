package br.com.dbc.vemser.dbcompras.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity {

    private Integer idItem;

    private String nome;

    private Integer quantidade;

    private CompraEntity compras;

}

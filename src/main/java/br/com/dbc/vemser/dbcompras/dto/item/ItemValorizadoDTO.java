package br.com.dbc.vemser.dbcompras.dto.item;

import lombok.Data;

@Data
public class ItemValorizadoDTO {
    private Integer idItem;
    private String nome;
    private Double valorUnitario;
    private Integer quantidade;
    private Double valorTotal;
}

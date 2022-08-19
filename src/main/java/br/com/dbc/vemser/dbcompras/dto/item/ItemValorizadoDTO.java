package br.com.dbc.vemser.dbcompras.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemValorizadoDTO {
    private Integer idItem;
    private String nome;
    private Double valorUnitario;
    private Integer quantidade;
    private Double valorTotal;
}

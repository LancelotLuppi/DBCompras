package br.com.dbc.vemser.dbcompras.dto.cotacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotacaoValorItensDTO {
    private Integer idItem;
    private Double valorDoItem;
}

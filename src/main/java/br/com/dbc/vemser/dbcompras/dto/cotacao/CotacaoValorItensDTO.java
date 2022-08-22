package br.com.dbc.vemser.dbcompras.dto.cotacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotacaoValorItensDTO {
    @NotNull
    private Integer idItem;
    @NotNull
    @Min(1)
    private Double valorDoItem;
}

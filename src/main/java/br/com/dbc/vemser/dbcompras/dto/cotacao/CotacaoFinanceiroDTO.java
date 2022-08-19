package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import java.util.Set;

@Data
public class CotacaoFinanceiroDTO {

    private Integer idCotacao;

    private String nome;

    private String anexo;

    private String status;

    private Set<ItemDTO> itens;

    private Double valorTotal;
}

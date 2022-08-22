package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoComItemDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComprasComCotacaoDTO {

    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDateTime dataCompra;
    private Double valorTotal;
    private StatusCompra status;
    private List<CotacaoComItemDTO> cotacoes;
}

package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CotacaoDTO extends CotacaoCreateDTO{
    private Integer idCotacao;
    private String nome;
    private LocalDateTime localDate;
    private String anexo;
    private Boolean status;
    private Double Valor;
    private CompraWithValorItensDTO compraDTO;
}

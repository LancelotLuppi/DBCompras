package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotacaoDTO extends CotacaoCreateDTO{
    private Integer idCotacao;
    private String nome;
    private LocalDateTime localDate;
    private String anexo;
    private String status;
    private Double valor;
    private CompraWithValorItensDTO compraDTO;
}

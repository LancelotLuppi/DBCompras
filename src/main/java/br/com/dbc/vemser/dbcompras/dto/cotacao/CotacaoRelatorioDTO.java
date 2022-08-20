package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotacaoRelatorioDTO {
    private Integer idCotacao;
    private String nome;
    private LocalDateTime localDate;
    private byte[] anexo;
    private StatusCotacao status;
    private Double Valor;
}

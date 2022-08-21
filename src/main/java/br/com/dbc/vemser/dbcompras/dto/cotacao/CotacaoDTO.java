package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDate;
    private String anexo;
    private StatusCotacao status;
    private Double valor;
    private CompraWithValorItensDTO compraDTO;

    public LocalDateTime getLocalDate() {
        return localDate;
    }
}

package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CotacaoComItemDTO {
    private Integer idCotacao;
    private String nome;
    private LocalDateTime localDate;
    private String anexo;
    private StatusCotacao status;
    private Double valor;
    private List<ItemValorizadoDTO> itemValorizadoDTOS;
}

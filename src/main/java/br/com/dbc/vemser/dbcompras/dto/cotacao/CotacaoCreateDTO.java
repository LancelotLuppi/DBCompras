package br.com.dbc.vemser.dbcompras.dto.cotacao;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CotacaoCreateDTO {

    private String nome;

    private String anexo;

    private Set<ItemDTO> itens;

}

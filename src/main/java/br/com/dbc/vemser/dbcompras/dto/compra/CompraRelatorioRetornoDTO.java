package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class CompraRelatorioRetornoDTO extends CompraRelatorioDTO {
    private List<ItemDTO> itens;
}

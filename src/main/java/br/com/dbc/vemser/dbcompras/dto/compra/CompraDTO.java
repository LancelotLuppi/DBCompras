package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompraDTO {
    private Integer idCompra;
    @NotBlank
    private String name;
    @NotBlank
    private String descricao;
    @NotNull
    private List<ItemDTO> itens;
}

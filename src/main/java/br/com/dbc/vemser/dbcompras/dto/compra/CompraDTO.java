package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
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
    private StatusCompra status;
    @NotNull
    private List<ItemDTO> itens;
}

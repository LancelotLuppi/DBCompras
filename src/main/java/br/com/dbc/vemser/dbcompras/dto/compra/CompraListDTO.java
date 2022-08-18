package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class CompraListDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDate dataCompra;
    private Double valorTotal;
    private List<ItemDTO> itens;
    private String status;
}

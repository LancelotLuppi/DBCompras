package br.com.dbc.vemser.dbcompras.dto.compra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraRelatorioDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDateTime dataCompra;
    private Double valorTotal;
    private String status;
}

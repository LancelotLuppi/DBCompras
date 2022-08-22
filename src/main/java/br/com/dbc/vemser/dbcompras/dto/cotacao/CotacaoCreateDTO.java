package br.com.dbc.vemser.dbcompras.dto.cotacao;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CotacaoCreateDTO {
    @NotBlank
    @NotNull
    private String nome;
    @Valid
    private List<CotacaoValorItensDTO> listaDeValores;
    @NotNull
    @NotBlank
    private String anexo;
}

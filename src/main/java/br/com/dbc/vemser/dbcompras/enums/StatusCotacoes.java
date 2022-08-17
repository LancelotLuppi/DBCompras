package br.com.dbc.vemser.dbcompras.enums;

public enum StatusCotacoes {

    APROVADO(true),
    REPROVADO(false);

    private Boolean situacaoCompra;

    public Boolean getSituacaoCompra() {
        return situacaoCompra;
    }

    StatusCotacoes(Boolean situacaoCompra) {
        this.situacaoCompra = situacaoCompra;
    }
}

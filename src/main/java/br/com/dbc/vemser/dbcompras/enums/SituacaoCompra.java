package br.com.dbc.vemser.dbcompras.enums;

public enum SituacaoCompra {
    ABERTO("aberto"),
    APROVADO_GESTOR("aprovado gestor"),
    REPROVADO_GESTOR("reprovado gestor"),
    APROVADO_FINANCEIRO("aprovado financeiro"),
    REPROVADO_FINANCEIRO("reprovado financeiro"),
    FECHADO("fechado");

    private String situacaoCompra;

    SituacaoCompra(String situacao) {
        this.situacaoCompra = situacao;
    }

    public String getSituacao() {
        return situacaoCompra;
    }
}

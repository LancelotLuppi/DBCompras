package br.com.dbc.vemser.dbcompras.enums;

public enum ControlarAcesso {
    ATIVAR (1), DESATIVAR(0);

    private Integer enable;

    ControlarAcesso(Integer enable){
        this.enable = enable;
    }

    public Integer getEnable(){
        return enable;
    }
}

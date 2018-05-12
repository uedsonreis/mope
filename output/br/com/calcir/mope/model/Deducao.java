
package br.com.calcir.mope.model;


public class Deducao {

    private Double valor;
    private Double teto;

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getTeto() {
        return teto;
    }

    public void setTeto(Double teto) {
        this.teto = teto;
    }

    public Double dedutivo() {
        if ((this.getTeto()!= null)&&(this.getValor()>this.getTeto())) {
            return this.getTeto();
        } else {
            return this.getValor();
        }
    }

}

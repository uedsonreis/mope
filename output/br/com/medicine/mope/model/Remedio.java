
package br.com.medicine.mope.model;


public class Remedio {

    private Integer codigo;
    private Integer quantidade;
    private String dosagem;
    private String principio;
    private String forma;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getPrincipio() {
        return principio;
    }

    public void setPrincipio(String principio) {
        this.principio = principio;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public void maisUm() {
        this.setQuantidade((this.getQuantidade()+ 1));
    }

    public void menosUm() {
        if (this.getQuantidade()> 0) {
            this.setQuantidade((this.getQuantidade()- 1));
        }
    }

}

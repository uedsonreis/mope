
package br.com.calcir.mope.model;

import java.util.ArrayList;
import java.util.List;

public class Pagador {

    private Double valorFaixa075;
    private Double valorFaixa150;
    private Double ganhoAnual;
    private Double valorFaixa275;
    private String cpf;
    private String nome;
    private Double valorFaixa225;
    private Double totalAPagar;
    private final List<Deducao> pagou = new ArrayList<Deducao>();
    public final static Double taxaPadraoINSS = 0.11D;
    public final static Double inssLimite1 = (1659.38D* 13);
    public final static Double inssLimite2 = (2765.66D* 13);
    public final static Double inssLimite3 = (5531.31D* 13);
    private Double valorFaixaIsento;

    public Double getValorFaixa075() {
        return valorFaixa075;
    }

    public void setValorFaixa075(Double valorFaixa075) {
        this.valorFaixa075 = valorFaixa075;
    }

    public Double getValorFaixa150() {
        return valorFaixa150;
    }

    public void setValorFaixa150(Double valorFaixa150) {
        this.valorFaixa150 = valorFaixa150;
    }

    public Double getGanhoAnual() {
        return ganhoAnual;
    }

    public void setGanhoAnual(Double ganhoAnual) {
        this.ganhoAnual = ganhoAnual;
    }

    public Double getValorFaixa275() {
        return valorFaixa275;
    }

    public void setValorFaixa275(Double valorFaixa275) {
        this.valorFaixa275 = valorFaixa275;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValorFaixa225() {
        return valorFaixa225;
    }

    public void setValorFaixa225(Double valorFaixa225) {
        this.valorFaixa225 = valorFaixa225;
    }

    public Double getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(Double totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public List<Deducao> getPagou() {
        return pagou;
    }

    public Double calcularINSS() {
        Double taxa = taxaPadraoINSS;
        if (this.getGanhoAnual()<= inssLimite1) {
            taxa = 0.08D;
        } else {
            if (this.getGanhoAnual()<= inssLimite2) {
                taxa = 0.09D;
            }
        }
        if (this.getGanhoAnual()>inssLimite3) {
            return (inssLimite3 *taxa);
        } else {
            return (this.getGanhoAnual()*taxa);
        }
    }

    public Double getValorFaixaIsento() {
        return valorFaixaIsento;
    }

    public void setValorFaixaIsento(Double valorFaixaIsento) {
        this.valorFaixaIsento = valorFaixaIsento;
    }

}

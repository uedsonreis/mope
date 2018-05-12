
package br.com.calcir.mope.model;

import java.util.ArrayList;
import java.util.List;

public class Tabela {

    private Double faixa225 = 10963.56D;
    private Integer anoExercicio = 2017;
    private Double faixaIsento = 22847.76D;
    private Double faixa075 = 11072.04D;
    private Double faixa150 = 11092.8D;
    private final List<Pagador> recebeuDe = new ArrayList<Pagador>();
    public final static Double P075 = 0.075D;
    public final static Double P150 = 0.15D;
    public final static Double P225 = 0.225D;
    public final static Double P275 = 0.275D;

    public Double getFaixa225() {
        return faixa225;
    }

    public void setFaixa225(Double faixa225) {
        this.faixa225 = faixa225;
    }

    public Integer getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Integer anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public Double getFaixaIsento() {
        return faixaIsento;
    }

    public void setFaixaIsento(Double faixaIsento) {
        this.faixaIsento = faixaIsento;
    }

    public Double getFaixa075() {
        return faixa075;
    }

    public void setFaixa075(Double faixa075) {
        this.faixa075 = faixa075;
    }

    public Double getFaixa150() {
        return faixa150;
    }

    public void setFaixa150(Double faixa150) {
        this.faixa150 = faixa150;
    }

    public List<Pagador> getRecebeuDe() {
        return recebeuDe;
    }

    public Double getTetoEducacao() {
        return  3561.5D;
    }

    public Double getTetoPGBL(Pagador pagador) {
        return (pagador.getGanhoAnual()* 0.12D);
    }

    public Boolean adicionar(Pagador pagador) {
        if (this.getRecebeuDe().contains(pagador)) {
            return false;
        } else {
            this.getRecebeuDe().add(pagador);
            return true;
        }
    }

    public Boolean remover(Pagador pagador) {
        if (this.getRecebeuDe().contains(pagador)) {
            this.getRecebeuDe().remove(pagador);
            return true;
        } else {
            return false;
        }
    }

    public void calcularIRPF(Pagador pagador) {
        Double totalAPagar = 0.0D;
        Double valorBase = (pagador.getGanhoAnual()-pagador.calcularINSS());
        for (Deducao deducao: pagador.getPagou()) {
            valorBase = (valorBase-deducao.dedutivo());
        }
        if (valorBase<= this.getFaixaIsento()) {
            pagador.setValorFaixaIsento(valorBase);
        } else {
            pagador.setValorFaixaIsento(this.getFaixaIsento());
            valorBase = (valorBase-this.getFaixaIsento());
            if (valorBase<= this.getFaixa075()) {
                pagador.setValorFaixa075(valorBase);
                totalAPagar = (valorBase*P075);
            } else {
                pagador.setValorFaixa075(this.getFaixa075());
                totalAPagar = (this.getFaixa075()*P075);
                valorBase = (valorBase-this.getFaixa075());
                if (valorBase<= this.getFaixa150()) {
                    pagador.setValorFaixa150(valorBase);
                    totalAPagar = (totalAPagar +(valorBase*P150));
                } else {
                    pagador.setValorFaixa150(this.getFaixa150());
                    totalAPagar = (totalAPagar +(this.getFaixa150()*P150));
                    valorBase = (valorBase-this.getFaixa150());
                    if (valorBase<= this.getFaixa225()) {
                        pagador.setValorFaixa225(valorBase);
                        totalAPagar = (totalAPagar +(valorBase*P225));
                    } else {
                        pagador.setValorFaixa225(this.getFaixa225());
                        totalAPagar = (totalAPagar +(this.getFaixa225()*P225));
                        valorBase = (valorBase-this.getFaixa225());
                        pagador.setValorFaixa275(valorBase);
                        totalAPagar = (totalAPagar +(valorBase*P275));
                    }
                }
            }
        }
        pagador.setTotalAPagar(totalAPagar);
        this.getRecebeuDe().add(pagador);
    }

}

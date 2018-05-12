
package br.com.medicine.mope.model;

import java.util.ArrayList;
import java.util.List;

public class Posto {

    private String logradouro;
    private String cep;
    private Double latitude;
    private String nome;
    private String bairro;
    private Integer numero;
    private Double longitude;
    private final List<Remedio> possui = new ArrayList<Remedio>();

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<Remedio> getPossui() {
        return possui;
    }

    public Boolean adicionar(Remedio remedio) {
        if (this.getPossui().contains(remedio)) {
            return false;
        } else {
            this.getPossui().add(remedio);
            return true;
        }
    }

    public Boolean remover(Remedio remedio) {
        if (this.getPossui().contains(remedio)) {
            this.getPossui().remove(remedio);
            return true;
        } else {
            return false;
        }
    }

}

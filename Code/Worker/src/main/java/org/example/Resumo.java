package org.example;

public class Resumo {
    // vars
    private String diretoria;

    // Construtores
    public Resumo() {
    }

    // construtor
    public Resumo(String diretoria) {
        this.diretoria = diretoria;
    }

    // get da categoria
    public String getResumo() {
        return diretoria;
    }

    // set da categoria
    public void setResumo(String diretoria) {
        this.diretoria = diretoria;
    }

}

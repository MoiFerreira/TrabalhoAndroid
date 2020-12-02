package com.example.compromisso;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Compromisso {
    private String titulo, descricao;
    private String data;
    private int id;

    public Compromisso(String titulo, String descricao, String data, int id) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data; //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(data);
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getData() {
        return data;
    }

    public int getId() { return id; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setId(int id) { this.id = id; }
}

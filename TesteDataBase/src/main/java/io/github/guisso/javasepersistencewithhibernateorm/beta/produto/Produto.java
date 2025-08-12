package io.github.guisso.javasepersistencewithhibernateorm.beta.produto;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Produto extends ProjectEntity implements Serializable {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = true, length = 255)
    private String descricao;

    @Column(nullable = false)
    private double peso;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(nullable = false)
    private int qtdEstoque;

    @Column(nullable = false)
    private boolean naLixeira = false;

    // Getters e setters

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public int getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(int qtdEstoque) { this.qtdEstoque = qtdEstoque; }

    public boolean isNaLixeira() { return naLixeira; }
    public void setNaLixeira(boolean naLixeira) { this.naLixeira = naLixeira; }

    // Métodos opcionais
    public void decrementarEstoque() {
        if (qtdEstoque > 0) {
            qtdEstoque--;
        }
    }

    public boolean isDisponivel() {
        return qtdEstoque > 0;
    }
}

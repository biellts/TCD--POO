package io.github.guisso.javasepersistencewithhibernateorm.beta.item;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Item extends ProjectEntity implements Serializable {

    @Column(nullable = false)
    private Integer pedidoId; // ID do pedido

    @Column(nullable = false)
    private Integer produtoId; // ID do produto

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private boolean naLixeira = false;

    // Getters e setters
    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public boolean isNaLixeira() { return naLixeira; }
    public void setNaLixeira(boolean naLixeira) { this.naLixeira = naLixeira; }

    // Método de negócio
    public BigDecimal calcularTotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}

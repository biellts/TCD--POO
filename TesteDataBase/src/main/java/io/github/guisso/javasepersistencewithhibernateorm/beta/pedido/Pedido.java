package io.github.guisso.javasepersistencewithhibernateorm.beta.pedido;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pedido extends ProjectEntity implements Serializable {

    @Column(nullable = false)
    private Integer clienteId; // apenas o ID do cliente

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private boolean naLixeira = false;

    public enum Status {
        CRIADO,
        PROCESSADO,
        ENVIADO,
        CANCELADO,
        PAGO,
        EM_PREPARACAO,
        PRONTO_ENVIO,
        ENTREGUE,
        REEMBOLSADO
    }

    // Getters e setters
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public boolean isNaLixeira() { return naLixeira; }
    public void setNaLixeira(boolean naLixeira) { this.naLixeira = naLixeira; }
}

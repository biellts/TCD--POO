package io.github.guisso.javasepersistencewithhibernateorm.beta.cliente;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.io.Serializable;

@Entity
public class Cliente extends ProjectEntity implements Serializable {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean naLixeira = false;

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isNaLixeira() { return naLixeira; }
    public void setNaLixeira(boolean naLixeira) { this.naLixeira = naLixeira; }
}

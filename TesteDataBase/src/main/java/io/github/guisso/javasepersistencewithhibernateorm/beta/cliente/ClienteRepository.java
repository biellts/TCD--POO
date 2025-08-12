/*
 * Copyright (C) 2025 Luis Guisso &lt;luis dot guisso at ifnmg dot edu dot br&gt;
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.guisso.javasepersistencewithhibernateorm.beta.cliente;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.DataSourceFactory;
import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ClienteRepository extends Repository<Cliente> {

    @Override
    public String getJpqlFindAll() {
        return "SELECT c FROM Cliente c WHERE c.naLixeira = false";
    }

    @Override
    public String getJpqlFindById() {
        return "SELECT c FROM Cliente c WHERE c.id = :id AND c.naLixeira = false";
    }

    @Override
    public String getJpqlDeleteById() {
        return "DELETE FROM Cliente c WHERE c.id = :id";
    }

    // ===== Métodos de lixeira =====
    public void moverParaLixeira(Cliente cliente) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                cliente.setNaLixeira(true);
                em.merge(cliente);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public void moverParaLixeiraPorId(Long id) {
        Cliente c = findById(id);
        if (c != null) moverParaLixeira(c);
    }

    public void moverParaLixeira(List<Cliente> clientes) {
        for (Cliente c : clientes) {
            moverParaLixeira(c);
        }
    }

    public List<Cliente> recuperarTodosDaLixeira() {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                "SELECT c FROM Cliente c WHERE c.naLixeira = true", Cliente.class
            ).getResultList();
        }
    }

    public Cliente recuperarDaLixeiraPorId(Long id) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                "SELECT c FROM Cliente c WHERE c.id = :id AND c.naLixeira = true", Cliente.class
            ).setParameter("id", id)
             .getSingleResult();
        }
    }

    public void restaurarDaLixeira(Cliente cliente) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                cliente.setNaLixeira(false);
                em.merge(cliente);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public void restaurarDaLixeiraPorId(Long id) {
        Cliente c = recuperarDaLixeiraPorId(id);
        if (c != null) restaurarDaLixeira(c);
    }

    public void restaurarTodosDaLixeira() {
        List<Cliente> clientes = recuperarTodosDaLixeira();
        for (Cliente c : clientes) {
            restaurarDaLixeira(c);
        }
    }

    public void excluirDefinitivo(Cliente cliente) {
        delete(cliente);
    }

    public void excluirDefinitivoPorId(Long id) {
        delete(id);
    }

    public void esvaziarLixeira() {
        List<Cliente> clientes = recuperarTodosDaLixeira();
        for (Cliente c : clientes) delete(c);
    }
}
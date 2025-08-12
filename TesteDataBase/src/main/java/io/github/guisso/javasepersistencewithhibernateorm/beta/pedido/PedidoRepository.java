package io.github.guisso.javasepersistencewithhibernateorm.beta.pedido;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.DataSourceFactory;
import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class PedidoRepository extends Repository<Pedido> {

    @Override
    public String getJpqlFindAll() {
        return "SELECT p FROM Pedido p WHERE p.naLixeira = false";
    }

    @Override
    public String getJpqlFindById() {
        return "SELECT p FROM Pedido p WHERE p.id = :id AND p.naLixeira = false";
    }

    @Override
    public String getJpqlDeleteById() {
        return "DELETE FROM Pedido p WHERE p.id = :id";
    }

    // Atualiza a entidade no banco, abrindo entity manager e transação
    public void update(Pedido pedido) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(pedido);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    // ====== Métodos de Lixeira ======

    public void moverParaLixeira(Pedido pedido) {
        pedido.setNaLixeira(true);
        update(pedido);
    }

    public void moverParaLixeiraPorId(Long id) {
        Pedido p = findById(id);
        if (p != null) moverParaLixeira(p);
    }

    public void moverColecaoParaLixeira(List<Pedido> pedidos) {
        for (Pedido p : pedidos) moverParaLixeira(p);
    }

    public List<Pedido> recuperarTodosDaLixeira() {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.naLixeira = true", Pedido.class
            ).getResultList();
        }
    }

    public Pedido recuperarDaLixeiraPorId(Long id) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.id = :id AND p.naLixeira = true", Pedido.class
            )
            .setParameter("id", id)
            .getSingleResult();
        }
    }

    public void restaurarDaLixeira(Pedido pedido) {
        pedido.setNaLixeira(false);
        update(pedido);
    }

    public void restaurarDaLixeiraPorId(Long id) {
        Pedido p = recuperarDaLixeiraPorId(id);
        if (p != null) restaurarDaLixeira(p);
    }

    public void excluirDefinitivo(Pedido pedido) {
        delete(pedido);
    }

    public void excluirDefinitivoPorId(Long id) {
        Pedido p = findById(id);
        if (p != null) delete(p);
    }

    public void esvaziarLixeira() {
        List<Pedido> pedidos = recuperarTodosDaLixeira();
        for (Pedido p : pedidos) delete(p);
    }
}

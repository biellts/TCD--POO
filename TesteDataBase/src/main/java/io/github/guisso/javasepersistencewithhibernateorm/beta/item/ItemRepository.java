package io.github.guisso.javasepersistencewithhibernateorm.beta.item;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.DataSourceFactory;
import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ItemRepository extends Repository<Item> {

    @Override
    public String getJpqlFindAll() {
        return "SELECT i FROM Item i WHERE i.naLixeira = false";
    }

    @Override
    public String getJpqlFindById() {
        return "SELECT i FROM Item i WHERE i.id = :id AND i.naLixeira = false";
    }

    @Override
    public String getJpqlDeleteById() {
        return "DELETE FROM Item i WHERE i.id = :id";
    }

    public void update(Item item) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(item);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    // ====== Métodos de Lixeira ======
    public void moverParaLixeira(Item item) {
        item.setNaLixeira(true);
        update(item);
    }

    public void moverParaLixeiraPorId(Long id) {
        Item i = findById(id);
        if (i != null) moverParaLixeira(i);
    }

    public void moverColecaoParaLixeira(List<Item> itens) {
        for (Item i : itens) moverParaLixeira(i);
    }

    public List<Item> recuperarTodosDaLixeira() {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT i FROM Item i WHERE i.naLixeira = true", Item.class
            ).getResultList();
        }
    }

    public Item recuperarDaLixeiraPorId(Long id) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT i FROM Item i WHERE i.id = :id AND i.naLixeira = true", Item.class
            )
            .setParameter("id", id)
            .getSingleResult();
        }
    }

    public void restaurarDaLixeira(Item item) {
        item.setNaLixeira(false);
        update(item);
    }

    public void restaurarDaLixeiraPorId(Long id) {
        Item i = recuperarDaLixeiraPorId(id);
        if (i != null) restaurarDaLixeira(i);
    }

    public void excluirDefinitivo(Item item) {
        delete(item);
    }

    public void excluirDefinitivoPorId(Long id) {
        Item i = findById(id);
        if (i != null) delete(i);
    }

    public void esvaziarLixeira() {
        List<Item> itens = recuperarTodosDaLixeira();
        for (Item i : itens) delete(i);
    }
}

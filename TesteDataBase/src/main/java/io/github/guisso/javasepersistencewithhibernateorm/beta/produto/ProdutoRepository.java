package io.github.guisso.javasepersistencewithhibernateorm.beta.produto;

import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.DataSourceFactory;
import io.github.guisso.javasepersistencewithhibernateorm.beta.repository.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ProdutoRepository extends Repository<Produto> {

    @Override
    public String getJpqlFindAll() {
        return "SELECT p FROM Produto p WHERE p.naLixeira = false";
    }

    @Override
    public String getJpqlFindById() {
        return "SELECT p FROM Produto p WHERE p.id = :id AND p.naLixeira = false";
    }

    @Override
    public String getJpqlDeleteById() {
        return "DELETE FROM Produto p WHERE p.id = :id";
    }

    public void update(Produto produto) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(produto);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    // ====== Métodos de Lixeira ======
    public void moverParaLixeira(Produto produto) {
        produto.setNaLixeira(true);
        update(produto);
    }

    public void moverParaLixeiraPorId(Long id) {
        Produto p = findById(id);
        if (p != null) moverParaLixeira(p);
    }

    public void moverColecaoParaLixeira(List<Produto> produtos) {
        for (Produto p : produtos) moverParaLixeira(p);
    }

    public List<Produto> recuperarTodosDaLixeira() {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT p FROM Produto p WHERE p.naLixeira = true", Produto.class
            ).getResultList();
        }
    }

    public Produto recuperarDaLixeiraPorId(Long id) {
        try (EntityManager em = DataSourceFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT p FROM Produto p WHERE p.id = :id AND p.naLixeira = true", Produto.class
            )
            .setParameter("id", id)
            .getSingleResult();
        }
    }

    public void restaurarDaLixeira(Produto produto) {
        produto.setNaLixeira(false);
        update(produto);
    }

    public void restaurarDaLixeiraPorId(Long id) {
        Produto p = recuperarDaLixeiraPorId(id);
        if (p != null) restaurarDaLixeira(p);
    }

    public void excluirDefinitivo(Produto produto) {
        delete(produto);
    }

    public void excluirDefinitivoPorId(Long id) {
        Produto p = findById(id);
        if (p != null) delete(p);
    }

    public void esvaziarLixeira() {
        List<Produto> produtos = recuperarTodosDaLixeira();
        for (Produto p : produtos) delete(p);
    }
}

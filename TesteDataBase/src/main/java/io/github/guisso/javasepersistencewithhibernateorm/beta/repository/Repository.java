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
package io.github.guisso.javasepersistencewithhibernateorm.beta.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Generic repository
 *
 * @author Luis Guisso &lt;luis dot guisso at ifnmg dot edu dot br&gt;
 * @version 0.1
 * @param <T> Type of objects
 * @since 0.1, Jul 7, 2025
 */
public abstract class Repository<T extends ProjectEntity>
        implements IRepository<T> {

    @Override
    public Long saveOrUpdate(T e) {

        // try-with resources
        try (EntityManager em = DataSourceFactory.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                if (e.getId() == null || e.getId() == 0) {
                    em.persist(e);
                } else {
                    em.merge(e);
                }
                tx.commit();
            } catch (Exception ex) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                    throw ex;
                }
            }
        }

        return e.getId();
    }

    @Override
    public List<T> findAll() {
        // try-with resources
        try (EntityManager em = DataSourceFactory.getEntityManager()) {

            TypedQuery<T> query = em.createQuery(
                    // Polimorphism applied
                    getJpqlFindAll(),
                    // Reflection to get .class type
                    (Class<T>) ((ParameterizedType) getClass()
                            .getGenericSuperclass())
                            .getActualTypeArguments()[0]);
            return query.getResultList();
        }
    }

    @Override
    public T findById(Long id) {
        // try-with resources
        try (EntityManager em = DataSourceFactory.getEntityManager()) {

            //
            // OPTION A
            //
            TypedQuery<T> query = em.createQuery(
                    // Polimorphism applied
                    getJpqlFindById(),
                    // Reflection to get .class type
                    (Class<T>) ((ParameterizedType) getClass()
                            .getGenericSuperclass())
                            .getActualTypeArguments()[0]);

            query.setParameter("id", id);

            // The result or null if there is no result
            List<T> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
            
            //
            // OPTION B
            //
//            return em.find(
//                    // Reflection to get .class type
//                    (Class<T>) ((ParameterizedType) getClass()
//                            .getGenericSuperclass())
//                            .getActualTypeArguments()[0], id);
        }
    }

    @Override
    public boolean delete(T e) {

        // Without JPQL version
        try (EntityManager em = DataSourceFactory.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                e = em.merge(e);
                em.remove(e);
                tx.commit();
                return true;

            } catch (Exception ex) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                    throw ex;
                }
            }

            return false;
        }
    }

    @Override
    public boolean delete(Long id) {

        // With JPQL version
        try (EntityManager em = DataSourceFactory.getEntityManager()) {

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                // No need for a typed query
                Query query = em.createQuery(getJpqlDeleteById());

                // id definition
                query.setParameter("id", id);

                // Perform the deletion
                int deletions = query.executeUpdate();

                tx.commit();
                return deletions > 0;

            } catch (Exception ex) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                    throw ex;
                }
            }

            return false;
        }
    }

}
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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Data source factory for MySQL RDBMS
 *
 * DDL: CREATE DATABASE gestaoescolarbeta DEFAULT CHARACTER SET utf8mb4 COLLATE
 * utf8mb4_unicode_ci;
 *
 * @author Luis Guisso &lt;luis dot guisso at ifnmg dot edu dot br&gt;
 * @version 0.1
 * @since 0.1, 2025-07-01
 */
public class DataSourceFactory {

    private static EntityManagerFactory emf;
    private static final String PU_NAME = "gestaoEscolarBetaPU";

    // EntityManagerFactory can have a persistent lifecycle
    private static EntityManagerFactory getEMFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PU_NAME);
        }

        return emf;
    }

    // EntityManager is not thread safe! A desktop application is
    // single threaded, but do not share the object for safety
    // (open and close in the processing method)
    public static EntityManager getEntityManager() {
        return DataSourceFactory.getEMFactory().createEntityManager();
    }
}

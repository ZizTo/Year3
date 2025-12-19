package com.zizto.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    public static void init() {
        try {
            if (ENTITY_MANAGER_FACTORY == null) {
                ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("phone-station-pu");
            }
        } catch (Throwable ex) {
            System.err.println("******************************************");
            System.err.println("CRITICAL ERROR: Initial SessionFactory creation failed.");
            System.err.println("******************************************");
            ex.printStackTrace();
            throw new RuntimeException("Ошибка инициализации БД", ex);
        }
    }

    public static EntityManager getEntityManager() {
        if (ENTITY_MANAGER_FACTORY == null) {
            init();
        }
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void shutdown() {
        if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}

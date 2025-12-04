package com.zizto.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    // Этот метод вызывает JpaListener при старте приложения
    public static void init() {
        try {
            if (ENTITY_MANAGER_FACTORY == null) {
                // Пытаемся подключиться к базе данных
                ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("phone-station-pu");
            }
        } catch (Throwable ex) {
            // В случае ошибки выводим её в консоль
            System.err.println("******************************************");
            System.err.println("CRITICAL ERROR: Initial SessionFactory creation failed.");
            System.err.println("******************************************");
            ex.printStackTrace(); // Показывает причину ошибки
            throw new RuntimeException("Ошибка инициализации БД", ex);
        }
    }

    public static EntityManager getEntityManager() {
        if (ENTITY_MANAGER_FACTORY == null) {
            // Если вдруг init() не вызвали, пробуем инициализировать тут
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

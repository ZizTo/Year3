package com.zizto.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JpaUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "phone-station-web-pu";
    private static EntityManagerFactory factory;

    public static void init() {
        if (factory == null) {
            try {
                LOGGER.info("Инициализация EntityManagerFactory для persistence unit: {}", PERSISTENCE_UNIT_NAME);
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                LOGGER.error("Ошибка при инициализации EntityManagerFactory", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static EntityManager getEntityManager() {
        if (factory == null) {
            throw new IllegalStateException("EntityManagerFactory не инициализирована");
        }
        return factory.createEntityManager();
    }

    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            LOGGER.info("Закрытие EntityManagerFactory.");
            factory.close();
        }
    }
}

package com.zizto.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилита для управления JPA EntityManagerFactory.
 * Использует singleton-паттерн для EntityManagerFactory, которая создается один раз
 * при старте веб-приложения. Жизненный цикл управляется JpaListener.
 */
public class JpaUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "phone-station-web-pu";
    private static EntityManagerFactory factory;

    /**
     * Инициализирует EntityManagerFactory.
     * Вызывается один раз из JpaListener при запуске приложения.
     */
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

    /**
     * Возвращает новый экземпляр EntityManager для выполнения операций с БД.
     * Каждый DAO-метод должен получать свой собственный EntityManager.
     *
     * @return новый экземпляр EntityManager
     */
    public static EntityManager getEntityManager() {
        if (factory == null) {
            throw new IllegalStateException("EntityManagerFactory не инициализирована. Убедитесь, что JpaListener активен.");
        }
        return factory.createEntityManager();
    }

    /**
     * Закрывает EntityManagerFactory.
     * Вызывается один раз из JpaListener при остановке приложения.
     */
    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            LOGGER.info("Закрытие EntityManagerFactory.");
            factory.close();
        }
    }
}

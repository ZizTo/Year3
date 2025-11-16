package com.zizto.dao;

import com.zizto.model.Subscriber;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberDao.class);

    public void blockSubscriber(int subscriberId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Subscriber subscriber = em.find(Subscriber.class, subscriberId);
            if (subscriber != null) {
                if (!subscriber.isBlocked()) {
                    subscriber.setBlocked(true);
                    LOGGER.info("Абонент с ID {} успешно заблокирован.", subscriberId);
                } else {
                    LOGGER.warn("Попытка заблокировать уже заблокированного абонента с ID {}.", subscriberId);
                }
            } else {
                LOGGER.warn("Абонент с ID {} не найден.", subscriberId);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            LOGGER.error("Ошибка при блокировке абонента с ID {}: ", subscriberId, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
}

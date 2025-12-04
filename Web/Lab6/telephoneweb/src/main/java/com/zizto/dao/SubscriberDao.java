package com.zizto.dao;

import com.zizto.model.Subscriber;
import com.zizto.model.Subscriber_;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SubscriberDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberDao.class);

    public List<Subscriber> findAll() {
        LOGGER.debug("Запрос на получение всех абонентов");
        try (EntityManager em = JpaUtil.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Subscriber> query = cb.createQuery(Subscriber.class);
            Root<Subscriber> subscriber = query.from(Subscriber.class);
            query.select(subscriber).orderBy(cb.asc(subscriber.get(Subscriber_.id)));
            return em.createQuery(query).getResultList();
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении списка абонентов", e);
            return Collections.emptyList();
        }
    }

    public Subscriber findById(int id) {
        LOGGER.debug("Поиск абонента по ID: {}", id);
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Subscriber.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске абонента по ID {}", id, e);
            throw new DaoException("Ошибка при поиске абонента по ID: " + id, e);
        }
    }

    public void blockSubscriber(int subscriberId) {
        LOGGER.debug("Попытка заблокировать абонента с ID: {}", subscriberId);
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Subscriber subscriber = em.find(Subscriber.class, subscriberId);
            if (subscriber != null) {
                if (!subscriber.isBlocked()) {
                    subscriber.setBlocked(true);
                    em.merge(subscriber); 
                    LOGGER.info("Абонент с ID {} успешно заблокирован.", subscriberId);
                } else {
                    LOGGER.warn("Попытка заблокировать уже заблокированного абонента с ID {}.", subscriberId);
                }
            } else {
                LOGGER.warn("Абонент с ID {} не найден для блокировки.", subscriberId);
                throw new DaoException("Абонент с ID " + subscriberId + " не найден.");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка при блокировке абонента с ID {}: ", subscriberId, e);
            throw new DaoException("Ошибка при блокировке абонента: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

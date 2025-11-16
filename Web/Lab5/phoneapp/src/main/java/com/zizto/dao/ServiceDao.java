package com.zizto.dao;

import com.zizto.model.Service;
import com.zizto.model.Subscriber;
import com.zizto.model.Subscriber_;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ServiceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDao.class);

    public List<Service> getAllServices() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT s FROM Service s", Service.class).getResultList();
        }
    }

    public List<Service> getServicesBySubscriberId(int subscriberId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Service> query = cb.createQuery(Service.class);
            Root<Subscriber> subscriber = query.from(Subscriber.class);

            // JOIN on the 'services' collection
            Join<Subscriber, Service> services = subscriber.join(Subscriber_.services);
            query.select(services)
                 .where(cb.equal(subscriber.get(Subscriber_.id), subscriberId));

            return em.createQuery(query).getResultList();
        } catch (Exception e) {
            LOGGER.error("Не удалось получить услуги для абонента с ID {}: ", subscriberId, e);
            return Collections.emptyList();
        }
    }
}


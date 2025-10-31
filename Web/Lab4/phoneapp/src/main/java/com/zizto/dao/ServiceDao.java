package com.zizto.dao;

import com.zizto.model.Service;
//import com.zizto.model.Subscriber;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ServiceDao {
    
    public List<Service> getAllServices() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT s FROM Service s", Service.class).getResultList();
        }
    }

    public List<Service> getServicesBySubscriberId(int subscriberId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createNamedQuery("Subscriber.findServicesBySubscriberId", Service.class)
                     .setParameter("subscriberId", subscriberId)
                     .getResultList();
        }
    }
}

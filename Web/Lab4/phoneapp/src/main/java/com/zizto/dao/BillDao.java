package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class BillDao {
    public Bill getUnpaidBillBySubscriberId(int subscriberId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            try {
                return em.createNamedQuery("Bill.findUnpaidBySubscriberId", Bill.class)
                         .setParameter("subscriberId", subscriberId)
                         .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }

    public void payBill(int billId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Bill bill = em.find(Bill.class, billId);
            if (bill != null) {
                bill.setPaid(true);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
}

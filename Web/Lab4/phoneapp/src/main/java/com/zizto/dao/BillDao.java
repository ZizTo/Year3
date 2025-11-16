package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.model.Bill_;
import com.zizto.model.Subscriber_;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillDao.class);

    public Bill getUnpaidBillBySubscriberId(int subscriberId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            try {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Bill> query = cb.createQuery(Bill.class);
                Root<Bill> bill = query.from(Bill.class);

                Predicate subscriberPredicate = cb.equal(bill.get(Bill_.subscriber).get(Subscriber_.id), subscriberId);
                Predicate paidPredicate = cb.isFalse(bill.get(Bill_.isPaid));

                query.where(cb.and(subscriberPredicate, paidPredicate));

                return em.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                LOGGER.info("Неоплаченных счетов для абонента с ID {} не найдено.", subscriberId);
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
                if (!bill.isPaid()) {
                    bill.setPaid(true);
                    LOGGER.info("Счет с ID {} успешно оплачен.", billId);
                } else {
                    LOGGER.warn("Попытка оплатить уже оплаченный счет с ID {}.", billId);
                }
            } else {
                LOGGER.warn("Счет с ID {} не найден.", billId);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            LOGGER.error("Ошибка при оплате счета с ID {}: ", billId, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
}


/*package com.zizto.dao;

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
}*/

package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.model.Bill_;
import com.zizto.model.Subscriber_;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
        LOGGER.debug("Поиск неоплаченного счета для абонента ID: {}", subscriberId);
        try (EntityManager em = JpaUtil.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Bill> query = cb.createQuery(Bill.class);
            Root<Bill> bill = query.from(Bill.class);

            Predicate subscriberPredicate = cb.equal(bill.get(Bill_.subscriber).get(Subscriber_.id), subscriberId);
            Predicate paidPredicate = cb.isFalse(bill.get(Bill_.isPaid));

            query.where(cb.and(subscriberPredicate, paidPredicate));

            return em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Неоплаченных счетов для абонента с ID {} не найдено.", subscriberId);
            return null;
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске неоплаченного счета для абонента ID {}", subscriberId, e);
            throw new DaoException("Ошибка при поиске неоплаченного счета", e);
        }
    }

    
    public void payBill(int billId) {
        LOGGER.debug("Попытка оплаты счета с ID: {}", billId);
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Bill bill = em.find(Bill.class, billId);
            if (bill != null) {
                if (!bill.isPaid()) {
                    bill.setPaid(true);
                    em.merge(bill);
                    LOGGER.info("Счет с ID {} успешно оплачен.", billId);
                } else {
                    LOGGER.warn("Попытка оплатить уже оплаченный счет с ID {}.", billId);
                }
            } else {
                LOGGER.warn("Счет с ID {} не найден для оплаты.", billId);
                throw new DaoException("Счет с ID " + billId + " не найден.");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка при оплате счета с ID {}: ", billId, e);
            throw new DaoException("Ошибка при оплате счета: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

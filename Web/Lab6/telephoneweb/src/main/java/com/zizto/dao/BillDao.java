package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillDao.class);

    public Bill getUnpaidBillBySubscriberId(int subscriberId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT b FROM Bill b WHERE b.subscriber.id = :subId AND b.isPaid = false";
            
            TypedQuery<Bill> query = em.createQuery(jpql, Bill.class);
            query.setParameter("subId", subscriberId);
            
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
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

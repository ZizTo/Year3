package com.zizto.dao;

/**
 * Пользовательское исключение для слоя DAO.
 * Оборачивает специфичные исключения JPA в ошибку, понятную для верхних слоев приложения.
 */
public class DaoException extends RuntimeException {
    
    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

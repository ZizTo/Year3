package com.ziz.exception;

public class DAOException extends RuntimeException {
    public DAOException(String msg, Throwable thr) {
        super(msg, thr);
    }
}

package com.zizto.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Date;

@StaticMetamodel(Bill.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Bill_ {

	
	/**
	 * @see com.zizto.model.Bill#isPaid
	 **/
	public static volatile SingularAttribute<Bill, Boolean> isPaid;
	
	/**
	 * @see com.zizto.model.Bill#amount
	 **/
	public static volatile SingularAttribute<Bill, BigDecimal> amount;
	
	/**
	 * @see com.zizto.model.Bill#subscriber
	 **/
	public static volatile SingularAttribute<Bill, Subscriber> subscriber;
	
	/**
	 * @see com.zizto.model.Bill#id
	 **/
	public static volatile SingularAttribute<Bill, Integer> id;
	
	/**
	 * @see com.zizto.model.Bill#issueDate
	 **/
	public static volatile SingularAttribute<Bill, Date> issueDate;
	
	/**
	 * @see com.zizto.model.Bill
	 **/
	public static volatile EntityType<Bill> class_;

	public static final String IS_PAID = "isPaid";
	public static final String AMOUNT = "amount";
	public static final String SUBSCRIBER = "subscriber";
	public static final String ID = "id";
	public static final String ISSUE_DATE = "issueDate";

}


package com.ziz.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Date;
import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Bill.class)
public abstract class Bill_ {

	public static volatile SingularAttribute<Bill, Boolean> isPaid;
	public static volatile SingularAttribute<Bill, BigDecimal> amount;
	public static volatile SingularAttribute<Bill, Subscriber> subscriber;
	public static volatile SingularAttribute<Bill, Integer> id;
	public static volatile SingularAttribute<Bill, Date> issueDate;

	public static final String IS_PAID = "isPaid";
	public static final String AMOUNT = "amount";
	public static final String SUBSCRIBER = "subscriber";
	public static final String ID = "id";
	public static final String ISSUE_DATE = "issueDate";

}


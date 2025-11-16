package com.ziz.model;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Subscriber.class)
public abstract class Subscriber_ {

	public static volatile SingularAttribute<Subscriber, String> phoneNumber;
	public static volatile SingularAttribute<Subscriber, Boolean> isBlocked;
	public static volatile SingularAttribute<Subscriber, String> fullName;
	public static volatile ListAttribute<Subscriber, Bill> bills;
	public static volatile SingularAttribute<Subscriber, Integer> id;
	public static volatile SetAttribute<Subscriber, Service> services;

	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String IS_BLOCKED = "isBlocked";
	public static final String FULL_NAME = "fullName";
	public static final String BILLS = "bills";
	public static final String ID = "id";
	public static final String SERVICES = "services";

}


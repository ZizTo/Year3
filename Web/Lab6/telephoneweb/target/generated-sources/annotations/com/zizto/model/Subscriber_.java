package com.zizto.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Subscriber.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Subscriber_ {

	
	/**
	 * @see com.zizto.model.Subscriber#phoneNumber
	 **/
	public static volatile SingularAttribute<Subscriber, String> phoneNumber;
	
	/**
	 * @see com.zizto.model.Subscriber#isBlocked
	 **/
	public static volatile SingularAttribute<Subscriber, Boolean> isBlocked;
	
	/**
	 * @see com.zizto.model.Subscriber#fullName
	 **/
	public static volatile SingularAttribute<Subscriber, String> fullName;
	
	/**
	 * @see com.zizto.model.Subscriber#bills
	 **/
	public static volatile ListAttribute<Subscriber, Bill> bills;
	
	/**
	 * @see com.zizto.model.Subscriber#id
	 **/
	public static volatile SingularAttribute<Subscriber, Integer> id;
	
	/**
	 * @see com.zizto.model.Subscriber#services
	 **/
	public static volatile SetAttribute<Subscriber, Service> services;
	
	/**
	 * @see com.zizto.model.Subscriber
	 **/
	public static volatile EntityType<Subscriber> class_;

	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String IS_BLOCKED = "isBlocked";
	public static final String FULL_NAME = "fullName";
	public static final String BILLS = "bills";
	public static final String ID = "id";
	public static final String SERVICES = "services";

}


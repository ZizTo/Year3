package com.zizto.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

@StaticMetamodel(Service.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Service_ {

	
	/**
	 * @see com.zizto.model.Service#name
	 **/
	public static volatile SingularAttribute<Service, String> name;
	
	/**
	 * @see com.zizto.model.Service#id
	 **/
	public static volatile SingularAttribute<Service, Integer> id;
	
	/**
	 * @see com.zizto.model.Service
	 **/
	public static volatile EntityType<Service> class_;
	
	/**
	 * @see com.zizto.model.Service#monthlyCost
	 **/
	public static volatile SingularAttribute<Service, BigDecimal> monthlyCost;

	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String MONTHLY_COST = "monthlyCost";

}


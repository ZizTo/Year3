package com.ziz.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Service.class)
public abstract class Service_ {

	public static volatile SingularAttribute<Service, String> name;
	public static volatile SingularAttribute<Service, Integer> id;
	public static volatile SingularAttribute<Service, BigDecimal> monthlyCost;

	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String MONTHLY_COST = "monthlyCost";

}


package com.zizto.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class User_ {

	
	/**
	 * @see com.zizto.model.User#password
	 **/
	public static volatile SingularAttribute<User, String> password;
	
	/**
	 * @see com.zizto.model.User#role
	 **/
	public static volatile SingularAttribute<User, Role> role;
	
	/**
	 * @see com.zizto.model.User#id
	 **/
	public static volatile SingularAttribute<User, Integer> id;
	
	/**
	 * @see com.zizto.model.User#login
	 **/
	public static volatile SingularAttribute<User, String> login;
	
	/**
	 * @see com.zizto.model.User
	 **/
	public static volatile EntityType<User> class_;

	public static final String PASSWORD = "password";
	public static final String ROLE = "role";
	public static final String ID = "id";
	public static final String LOGIN = "login";

}


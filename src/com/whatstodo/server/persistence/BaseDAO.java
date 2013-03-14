package com.whatstodo.server.persistence;

import java.util.List;

import com.mongodb.MongoClient;

public interface BaseDAO<T> {
	
	void open(MongoClient client, String name, String collection);
	
	void close();
	
	T getById(long id);
	
	List<T> findAll();
	
	T create(T entity);
	
	T read(long id);
	
	T update(T entity);
	
	void delete(T entity);

}

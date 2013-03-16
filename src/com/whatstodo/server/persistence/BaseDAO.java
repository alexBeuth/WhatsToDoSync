package com.whatstodo.server.persistence;

import java.util.List;

public interface BaseDAO<T> {

	void open(String collection);

	void close();

	T create(T entity);

	T read(T entity);

	T update(T entity);

	void delete(T entity);

	List<T> findAll();

	T getById(long id);

	List<T> updateAll(List<T> entities);

	void deleteAll();

}

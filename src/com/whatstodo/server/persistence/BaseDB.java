package com.whatstodo.server.persistence;

import java.util.List;

import com.mongodb.MongoClient;

public interface BaseDB<T> {

	void open(MongoClient client, String name, String collection);
	
	void addEntry(T entry);
	
	T getById(T entry);
	
	List<T> getAll();
	
	void update(T oldEntry, T newEntry);
	
	void deleteAll();
	
	void deleteEntry(T entry);

}

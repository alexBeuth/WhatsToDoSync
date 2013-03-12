package com.whatstodo.server.persistence;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class MongoDB implements BaseDB<BasicDBObject>{

	private DB db;
	private DBCollection collection;
	
	public void open(MongoClient client, String name, String collection) {
		this.db = client.getDB(name);
		this.setCollection(db.getCollection(collection));
	}

	public void addEntry(BasicDBObject entry) {
		//TODO check if there is already one entry with this id
		collection.insert(entry);
		//System.out.println("New Entry:" + entry.toString());
		return;
	}

	public BasicDBObject getById(BasicDBObject entry) {
		return (BasicDBObject)collection.findOne(entry);
	}

	public List<BasicDBObject> getAll() {
		
		List<BasicDBObject> resultList = new ArrayList<BasicDBObject>();
		DBCursor cursor = this.collection.find();
		//resultList.add((BasicDBObject)collection.findOne());
		try {
		   while(cursor.hasNext()) {
			   resultList.add((BasicDBObject)cursor.next());
		       //System.out.println(cursor.next());
		   }
		} finally {
		   cursor.close();
		}
		return resultList;
	}

	public void update(BasicDBObject newEntry, BasicDBObject oldEntry) {
		collection.update(oldEntry, newEntry, true, false);
		return;
	}
	
	
	public void deleteAll(){
		collection.drop();
	}

	public void deleteEntry(BasicDBObject entry) {
		collection.remove(entry);
	}

	public DBCollection getCollection() {
		return collection;
	}

	public void setCollection(DBCollection collection) {
		this.collection = collection;
	}
}
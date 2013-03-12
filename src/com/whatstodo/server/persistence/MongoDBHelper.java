package com.whatstodo.server.persistence;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.entrib.mongo2gson.Mongo2gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoHelper {

	private String dbName;
	private String dbCollection;
	private String mongoDBIp;
	private int mongoDBPort;
	private MongoTest mongo = new MongoTest();

	public MongoHelper(String name, String collection, String ip, int port) {
		this.dbName = name;
		this.dbCollection = collection;
		this.mongoDBIp = ip;
		this.mongoDBPort = port;
	}

	public void addObject(JsonObject json) {

		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			Object o = com.mongodb.util.JSON.parse(json.toString());
			mongo.addEntry((BasicDBObject) o);
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public List<JsonObject> findAll() {

		List<BasicDBObject> dbList = new ArrayList<BasicDBObject>();
		List<JsonObject> resultList = new ArrayList<JsonObject>();

		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			dbList = mongo.getAll();

			// TODO check null pointer
			if (dbList == null)
				throw new IllegalArgumentException(
						"There is currently no entry");

			Mongo2gson mongo2gson = new Mongo2gson();
			for (BasicDBObject item : dbList) {
				resultList.add(mongo2gson.getAsJsonObject(item));
			}
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public JsonObject findById(long id) {
		JsonObject json = new JsonObject();
		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			Mongo2gson mongo2gson = new Mongo2gson();
			BasicDBObject basicObj = mongo
					.getById(new BasicDBObject("_id", id));

			// TODO check null pointer
			if (basicObj == null)
				throw new IllegalArgumentException("There is no entry with id="
						+ id);

			json = (mongo2gson.getAsJsonObject(basicObj));
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return json;
	}

	public void update(JsonObject json, long id) {
		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			JsonObject newEntry = new JsonObject();
			newEntry.addProperty("_id", id);
			Object o = com.mongodb.util.JSON.parse(json.toString());
			mongo.update((BasicDBObject) o,
					new BasicDBObject().append("_id", id));
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public void deleteAll() {
		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			mongo.deleteAll();
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public void deleteEntry(long id) {
		try {
			MongoClient client = new MongoClient(this.mongoDBIp,
					this.mongoDBPort);
			mongo.open(client, dbName, dbCollection);
			mongo.deleteEntry(new BasicDBObject("_id", id));
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}
package com.whatstodo.server.persistence;

import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.whatstodo.dtos.ListDTO;

public class TodoDAOMongoDB implements TodoDAO {

	private String dbName;
	private String dbCollection;
	private String mongoDBIp;
	private int mongoDBPort;
	private MongoDB mongo = new MongoDB();
	private MongoClient client;

	private Gson gson = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.STATIC).serializeNulls()
			.create();

	public TodoDAOMongoDB(String name, String ip, int port) {
		this.dbName = name;
		this.mongoDBIp = ip;
		this.mongoDBPort = port;
	}

	@Override
	public void open(String collection) {

		this.dbCollection = collection;
		try {
			this.setClient(new MongoClient(this.mongoDBIp, this.mongoDBPort));
			mongo.open(client, dbName, dbCollection);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {

		client.close();
	}

	@Override
	public ListDTO create(ListDTO entity) {

		Object o = com.mongodb.util.JSON.parse(gson.toJson(entity));
		mongo.addEntry((BasicDBObject) o);
		return read(entity);
	}

	@Override
	public ListDTO read(ListDTO entity) {

		return getById(entity.getId());
	}

	@Override
	public ListDTO update(ListDTO entity) {

		Object o = JSON.parse(gson.toJson(entity));
		mongo.update((BasicDBObject) o,
				new BasicDBObject().append("_id", entity.getId()));
		return read(entity);
	}

	@Override
	public void delete(ListDTO entity) {

		mongo.deleteEntry(new BasicDBObject("_id", entity.getId()));
	}

	@Override
	public List<ListDTO> findAll() {

		List<BasicDBObject> dbList = new ArrayList<BasicDBObject>();
		List<ListDTO> resultList = new ArrayList<ListDTO>();

		dbList = mongo.getAll();
		if (dbList == null)
			return null;
		for (BasicDBObject item : dbList) {
			ListDTO dto = gson.fromJson(item.toString(), ListDTO.class);
			resultList.add(dto);
		}
		return resultList;
	}

	@Override
	public ListDTO getById(long id) {

		ListDTO listDTO = new ListDTO();

		BasicDBObject basicObj = mongo.getElement(new BasicDBObject("_id", id));
		if (basicObj == null)
			return null;
		listDTO = gson.fromJson(basicObj.toString(), ListDTO.class);
		return listDTO;
	}

	@Override
	public List<ListDTO> updateAll(List<ListDTO> entities) {

		for (ListDTO item : entities) {
			update(item);
		}
		return findAll();
	}

	@Override
	public void deleteAll() {

		mongo.deleteAll();
	}

	public MongoClient getClient() {
		return client;
	}

	public void setClient(MongoClient client) {
		this.client = client;
	}
}
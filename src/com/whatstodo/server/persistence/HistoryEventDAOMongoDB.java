package com.whatstodo.server.persistence;

import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.models.HistoryEvent.Type;

public class HistoryEventDAOMongoDB implements HistoryEventDAO {

	private String dbName;
	private String dbCollection;
	private String mongoDBIp;
	private int mongoDBPort;
	private MongoDB mongo = new MongoDB();
	private MongoClient client;

	private Gson gson = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.STATIC).serializeNulls()
			.create();

	public HistoryEventDAOMongoDB(String name, String ip, int port) {

		this.dbName = name;
		this.mongoDBIp = ip;
		this.mongoDBPort = port;
	}

	@Override
	public void open(String collection) {

		this.dbCollection = collection + "History";
		try {
			this.setClient(new MongoClient(this.mongoDBIp, this.mongoDBPort));
			mongo.open(getClient(), dbName, dbCollection);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {

		client.close();
	}

	@Override
	public HistoryEvent create(HistoryEvent entity) {

		Object o = com.mongodb.util.JSON.parse(gson.toJson(entity));
		mongo.addEntry((BasicDBObject) o);
		return read(entity);
	}

	@Override
	public HistoryEvent read(HistoryEvent entity) {

		return getById(entity.getId());
	}

	@Override
	public HistoryEvent update(HistoryEvent entity) {
		throw new UnsupportedOperationException(
				"This operation is not supported");
	}

	@Override
	public void delete(HistoryEvent entity) {

		throw new UnsupportedOperationException(
				"This operation is not supported");
	}

	@Override
	public List<HistoryEvent> findAll() {

		List<BasicDBObject> dbList = new ArrayList<BasicDBObject>();
		List<HistoryEvent> resultList = new ArrayList<HistoryEvent>();

		dbList = mongo.getAll();
		if (dbList == null)
			return null;
		for (BasicDBObject item : dbList) {
			HistoryEvent historyEvent = gson.fromJson(item.toString(),
					HistoryEvent.class);
			resultList.add(historyEvent);
		}
		return resultList;
	}

	@Override
	public HistoryEvent getById(long id) {

		HistoryEvent historyEvent = new HistoryEvent();

		BasicDBObject basicObj = mongo.getElement(new BasicDBObject("_id", id));
		if (basicObj == null)
			return null;
		historyEvent = gson.fromJson(basicObj.toString(), HistoryEvent.class);
		return historyEvent;
	}

	@Override
	public List<HistoryEvent> updateAll(List<HistoryEvent> entities) {

		throw new UnsupportedOperationException(
				"This operation is not supported");
	}

	@Override
	public void deleteAll() {

		mongo.deleteAll();
	}

	@Override
	public List<HistoryEvent> find(Type type, Long entityUid, Long parentEntityUid, Action action,
			Date after, Boolean isSynchronized) {

		List<BasicDBObject> dbList = new ArrayList<BasicDBObject>();
		List<HistoryEvent> resultList = new ArrayList<HistoryEvent>();
		BasicDBObject query = new BasicDBObject();

		if (type != null)
			query.put("type", type.toString());
		if (entityUid != null)
			query.append("entityUid", entityUid);
		if (parentEntityUid != null)
			query.append("parentEntityUid", parentEntityUid);
		if (action != null)
			query.append("action", action.toString());
		if (after != null)
			query.put("timeOfChange",
					new BasicDBObject("$gte", after.toString()));
		if (isSynchronized != null)
			query.append("isSynchronized", isSynchronized);

		dbList = mongo.getElements(query);
		if (dbList == null)
			return null;
		for (BasicDBObject item : dbList) {
			HistoryEvent event = gson.fromJson(item.toString(),
					HistoryEvent.class);
			resultList.add(event);
		}
		return resultList;
	}

	public MongoClient getClient() {
		return client;
	}

	public void setClient(MongoClient client) {
		this.client = client;
	}
}
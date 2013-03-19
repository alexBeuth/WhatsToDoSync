package com.whatstodo.server.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.models.HistoryEvent.Type;
import com.whatstodo.server.persistence.HistoryEventDAO;
import com.whatstodo.server.persistence.HistoryEventDAOMongoDB;

public class HistoryEventManager {

	private static HistoryEventManager instance = new HistoryEventManager();

	private HistoryEventDAO historyEventDao;

	private HistoryEventManager() {

		historyEventDao = new HistoryEventDAOMongoDB("WhatsToDo", "localhost",
				27017);
	}

	public static HistoryEventManager getInstance() {

		return instance;
	}

	public HistoryEvent save(HistoryEvent listToSave, String userUID) {
		try {
			historyEventDao.open(userUID);
			HistoryEvent listToReturn = historyEventDao.read(listToSave);
			if (listToReturn == null)
				listToReturn = historyEventDao.create(listToSave);
			return listToReturn;
		} finally {
			historyEventDao.close();
		}
	}

	public List<HistoryEvent> load(Type type, Long entityUid,
			Long parentEntityUid, Action action, Date timeOfChange,
			Boolean isSynchronized, String userUID) {

		List<HistoryEvent> listEvent = new ArrayList<HistoryEvent>();
		try {
			historyEventDao.open(userUID);
			// listEvent = historyEventDao.find(list.getType(),
			// list.getEntityUid(), list.getAction(),
			// list.getTimeOfChange(), list.isSynchronized());
			listEvent = historyEventDao.find(type, entityUid, parentEntityUid,
					action, timeOfChange, isSynchronized);
		} finally {
			historyEventDao.close();
		}

		return listEvent;
	}

	public List<HistoryEvent> getAllDTOs(String userID) {
		List<HistoryEvent> resultList = new ArrayList<HistoryEvent>();
		try {
			historyEventDao.open(userID);
			resultList = historyEventDao.findAll();
		} finally {
			historyEventDao.close();
		}
		return resultList;
	}

	public void deleteAll(String userID) {
		try {
			historyEventDao.open(userID);
			historyEventDao.deleteAll();
		} finally {
			historyEventDao.close();
		}
	}
}
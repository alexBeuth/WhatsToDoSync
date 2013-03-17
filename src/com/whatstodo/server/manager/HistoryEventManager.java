package com.whatstodo.server.manager;

import java.util.ArrayList;
import java.util.List;

import com.whatstodo.models.HistoryEvent;
import com.whatstodo.server.persistence.HistoryEventDAO;
import com.whatstodo.server.persistence.HistoryEventDAOMongoDB;

public class HistoryEventManager {

	private static HistoryEventManager instance = new HistoryEventManager();

	private HistoryEventDAO historyEventDao;

	private HistoryEventManager() {

		historyEventDao = new HistoryEventDAOMongoDB("test", "localhost",
				27017);
	}

	public static HistoryEventManager getInstance(String userUID) {

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

	public List<HistoryEvent> load(HistoryEvent list, String userUID) {

		List<HistoryEvent> listE = new ArrayList<HistoryEvent>();
		try {
			historyEventDao.open(userUID);
			listE = historyEventDao.find(list.getType(), list.getEntityUid(),
					list.getAction(), list.getTimeOfChange(),
					list.isSynchronized());
		} finally {
			historyEventDao.close();
		}

		return listE;
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
package com.whatstodo.manager;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.server.persistence.TodoDAO;

public class TodoManager {

	private static TodoManager instance = new TodoManager();

	private TodoDAO todoDao;

	private TodoManager() {

		// TODO Here goes the implementation of the dao
		todoDao = null;
	}

	public static TodoManager getInstance(String userUID) {

		return instance;
	}

	public ListDTO save(ListDTO listToSave, String userUID) {

		try {
			//TODO replace the null values with sth. useful
			todoDao.open(null, null, userUID);
			ListDTO listToReturn = todoDao.getById(listToSave.getId());
			if (listToReturn == null) {
				listToReturn = todoDao.create(listToSave);
			} else {
				listToReturn = todoDao.update(listToSave);
			}
			return listToReturn;
		} finally {
			todoDao.close();
		}
	}

	public ListDTO load(long id, String userUID) {
		try {
			todoDao.open(null, null, userUID);
			return todoDao.getById(id);
		} finally {
			todoDao.close();
		}
	}
	
	public void delete(ListDTO list, String userUID) {
		try {
			todoDao.open(null, null, userUID);
			todoDao.delete(list);
		} finally {
			todoDao.close();
		}
	}
}

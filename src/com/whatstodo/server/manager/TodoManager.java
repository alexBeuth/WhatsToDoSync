package com.whatstodo.server.manager;

import java.util.ArrayList;
import java.util.List;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.server.persistence.TodoDAO;
import com.whatstodo.server.persistence.TodoDAOMongoDB;

public class TodoManager {

	private static TodoManager instance = new TodoManager();

	private TodoDAO todoDao;

	private TodoManager() {

		todoDao = new TodoDAOMongoDB("WhastToDo", "localhost", 27017);
	}

	public static TodoManager getInstance() {

		return instance;
	}

	public ListDTO save(ListDTO listToSave, String userUID) {

		try {
			// TODO replace the null values with something useful
			todoDao.open(userUID);
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
			todoDao.open(userUID);
			return todoDao.getById(id);
		} finally {
			todoDao.close();
		}
	}

	public void delete(ListDTO list, String userUID) {
		try {
			todoDao.open(userUID);
			todoDao.delete(list);
		} finally {
			todoDao.close();
		}
	}

	public List<ListDTO> getAllDTOs(String userID) {
		List<ListDTO> resultList = new ArrayList<ListDTO>();
		try {
			todoDao.open(userID);
			resultList = todoDao.findAll();
		} finally {
			todoDao.close();
		}
		return resultList;
	}

	public void deleteAll(String userID) {
		try {
			todoDao.open(userID);
			todoDao.deleteAll();
		} finally {
			todoDao.close();
		}
	}
}

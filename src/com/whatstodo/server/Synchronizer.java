package com.whatstodo.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.net.request.SyncAllRequest;
import com.whatstodo.net.request.SyncTodoRequest;
import com.whatstodo.server.manager.HistoryEventManager;
import com.whatstodo.server.manager.TodoManager;

public class Synchronizer {

	HistoryEventManager dbHistory;
	TodoManager dbTodo;

	public Synchronizer() {

		this.dbHistory = HistoryEventManager.getInstance();
		this.dbTodo = TodoManager.getInstance();
	}

	public ListDTO synchronizeList(String user, long listId,
			SyncTodoRequest request) {
		
		ListDTO listDTO = new ListDTO(); 
		List<HistoryEvent> listDBEvent = new ArrayList<HistoryEvent>();	
		List<HistoryEvent> listSyncEvent = request.getHistory();

		for(HistoryEvent item : listSyncEvent){
			if(listId == item.getEntityUid()){
				listDBEvent = dbHistory.load(null, listId, null, null,
						item.getTimeOfChange(), null, user);
				
				if (listDBEvent == null) {
					//Sync of the server
					if (item.getAction() == Action.Created
							|| item.getAction() == Action.Updated) {
						dbTodo.save(request.getTodo(), user);
					} else if (item.getAction() == Action.Deleted) {
						dbTodo.delete(dbTodo.load(listId, user), user);
					}
					saveDBEvent(item, user);
				}
				else {
					//Sync of the app
					for (HistoryEvent event : listDBEvent) {
						if (event.getAction() == Action.Created) {
							//TODO Created is not possible in this case
						} else if (event.getAction() == Action.Updated) {
							listDTO = dbTodo.load(listId, user);
						} else if (event.getAction() == Action.Deleted) {
							// TODO send a deleted list
						}
					}
				}
			}
		}
		return listDTO;
	}

	public List<ListDTO> synchronizeAll(String user, SyncAllRequest request) {

		List<ListDTO> syncList = new ArrayList<ListDTO>();
		List<HistoryEvent> listDBEvent = new ArrayList<HistoryEvent>();
		List<HistoryEvent> listDBTodo = dbHistory.getAllDTOs(user);
		List<HistoryEvent> listSyncEvent = request.getHistory();

		for (HistoryEvent item : listSyncEvent) {
			listDBEvent = dbHistory.load(null, item.getEntityUid(), null, null,
					item.getTimeOfChange(), null, user);

			if (listDBEvent == null) {
				if (item.getAction() == Action.Created
						|| item.getAction() == Action.Updated) {
					ListDTO listDTO = getListById(request.getTodos(),
							item.getEntityUid());
					dbTodo.save(listDTO, user);
				} else if (item.getAction() == Action.Deleted) {
					dbTodo.delete(dbTodo.load(item.getEntityUid(), user), user);
				}
				saveDBEvent(item, user);
			}

			else {
				for (HistoryEvent event : listDBEvent) {
					if (event.getAction() == Action.Created) {
						//TODO Created is not possible in this case
					} else if (event.getAction() == Action.Updated) {
						syncList.add(dbTodo.load(event.getEntityUid(), user));
					} else if (event.getAction() == Action.Deleted) {
						// TODO send a deleted list
					}
				}
			}
		}
		for(HistoryEvent eventDB : listDBTodo){
			//TODO In this case we have to remember the time of the last sync - is a better solution as to send all lists
			if(eventDB.getAction() == Action.Created){
				if(getListById(request.getTodos(), eventDB.getEntityUid()) == null){
					syncList.add(dbTodo.load(eventDB.getEntityUid(), user));
				}
			}
		}
		return syncList;
	}

	public ListDTO getListById(List<ListDTO> listDTOs, Long id) {

		for (ListDTO item : listDTOs) {
			if (item.getId() == id)
				return item;
		}
		return null;
	}

	public void saveDBEvent(HistoryEvent newEvent, String user) {
		newEvent.setSynchronized(true);
		newEvent.setTimeOfChange(Calendar.getInstance().getTime());
		dbHistory.save(newEvent, user);
	}
}

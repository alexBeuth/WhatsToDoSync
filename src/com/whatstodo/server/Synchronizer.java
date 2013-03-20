package com.whatstodo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.models.Task;
import com.whatstodo.net.request.SyncAllRequest;
import com.whatstodo.server.manager.HistoryEventManager;
import com.whatstodo.server.manager.TodoManager;

public class Synchronizer {

	HistoryEventManager dbHistory;
	TodoManager dbTodo;

	public Synchronizer() {

		this.dbHistory = HistoryEventManager.getInstance();
		this.dbTodo = TodoManager.getInstance();
	}

	@SuppressWarnings("unused")
	public ListDTO synchronizeList(String user, ListDTO todo,
			List<HistoryEvent> history) {
		
		com.whatstodo.models.List syncedTodo = null;
		ListDTO serverDTO = dbTodo.load(todo.getId(), user);
		com.whatstodo.models.List oldTodo = new com.whatstodo.models.List();
		if (serverDTO != null) {
			oldTodo = com.whatstodo.models.List
					.fromDTO(serverDTO);
		}
		com.whatstodo.models.List appTodo = com.whatstodo.models.List.fromDTO(todo);

		if (history != null && !history.isEmpty()) {
			// sort the collection. The oldest event should be on top
			Collections.sort(history);
			
			
			List<HistoryEvent> serverHistory =  new ArrayList<HistoryEvent>();
			
			// Load the old events for the Todo since the last sync
			List<HistoryEvent> todoHistory = dbHistory.load(null, todo.getId(),
					null, null, history.get(0).getTimeOfChange(),
					null, user);
			// Load the old events for the Tasks since the last sync
			List<HistoryEvent> taskHistory = dbHistory.load(null, null,
					todo.getId(), null, history.get(0).getTimeOfChange(),
					null, user);
			
			serverHistory.addAll(taskHistory);
			serverHistory.addAll(todoHistory);

			if (false) {//serverHistory.isEmpty()) {
				// Nothing happened on the server. App has the latest version
				syncedTodo = appTodo;
				for (HistoryEvent event : history) {
					saveDBEvent(event, user);
				}

			} else {
				// Both sides have changes now merge!

				// Merge both histories
				history.addAll(serverHistory);
				Collections.sort(history);

				syncedTodo = merge(user, history, oldTodo, appTodo);
			}

		} else {
			// History is empty so the list on the server is the newest

			syncedTodo = com.whatstodo.models.List.fromDTO(dbTodo.load(appTodo.getId(), user));
		}

		dbTodo.save(com.whatstodo.models.List.toDTO(syncedTodo), user);
		return com.whatstodo.models.List.toDTO(syncedTodo);
	}

	private com.whatstodo.models.List merge(String user,
			List<HistoryEvent> history, com.whatstodo.models.List serverTodo,
			com.whatstodo.models.List appTodo) {
		com.whatstodo.models.List syncedTodo;
		
		//Use te server version as starting point
		syncedTodo = serverTodo;
		
		// Iterate over history starting at oldest entry
		for (HistoryEvent event : history) {

			switch (event.getType()) {
			case Task:
				mergeTask(syncedTodo, serverTodo, appTodo, event);
				break;
			case Todo:
				switch (event.getAction()) {
				case Deleted:
					//This shouldn't happen
					break;
				case Created:
				case Updated:
					if(!event.isSynchronized()) {
						//the event is not synchronized so the data need to be added from the appTodo
						updateTodo(syncedTodo, appTodo);
					} else {
						//the event is synchronized so the data need to be added from the oldTodo
						updateTodo(syncedTodo, serverTodo);
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			
			saveDBEvent(event, user);
		}
		return syncedTodo;
	}

	private void mergeTask(com.whatstodo.models.List syncedTodo,
			com.whatstodo.models.List serverTodo,
			com.whatstodo.models.List appTodo, HistoryEvent event) {
		
		switch (event.getAction()) {
		case Created:
			if(!event.isSynchronized()) {
				//the event is not synchronized so the task need to be added from the appTodo
				addTaskToSyncedList(event.getEntityUid(), appTodo, syncedTodo);
			} else {
				//the event is synchronized so the task need to be added from the oldTodo
				addTaskToSyncedList(event.getEntityUid(), serverTodo, syncedTodo);
			}
			break;
			
		case Updated:
			if(!event.isSynchronized()) {
				//the event is not synchronized so the task need to be added from the appTodo
				
				updateTaskInSyncedList(event.getEntityUid(), appTodo, syncedTodo);
			} else {
				//the event is synchronized so the task need to be added from the oldTodo
				updateTaskInSyncedList(event.getEntityUid(), serverTodo, syncedTodo);
			}
			break;
		case Deleted:
			
			//Delete task from todo
			deleteTaskFromSyncedList(event.getEntityUid(), syncedTodo);
			break;
		default:
			break;
		}
	}

	private void updateTodo(com.whatstodo.models.List to,
			com.whatstodo.models.List from) {
		to.setId(from.getId());
		to.setName(from.getName());
		//to.setSize(from.size());
	}

	private void deleteTaskFromSyncedList(long taskId,
			com.whatstodo.models.List syncedTodo) {
		
		Task dummyTask = new Task();
		dummyTask.setId(taskId);
		if(syncedTodo.contains(dummyTask)) {
			syncedTodo.remove(syncedTodo.indexOf(dummyTask));
		} else {
			//not there can't delete. Do nothing
		}
		
	}

	private void updateTaskInSyncedList(long taskId,
			com.whatstodo.models.List from,
			com.whatstodo.models.List to) {
		
		//get the updated todo
		Task dummyTask = new Task();
		dummyTask.setId(taskId);
		if(from.contains(dummyTask)) {
			Task task = from.getTask(taskId);
			
			if(to.contains(task)) {
				//Replace the task if it was added
				to.remove(to.indexOf(dummyTask));
			}
			//Just add
			to.add(task);
			
		} else {
			//The event was deleted after this event. do nothing
		}
	}

	private void addTaskToSyncedList(long taskId,
			com.whatstodo.models.List from,
			com.whatstodo.models.List to) {
		
		//get the created todo
		Task dummyTask = new Task();
		dummyTask.setId(taskId);
		if(from.contains(dummyTask)) {
			Task task = from.getTask(taskId);
			
			//Check for id conflict
			while(to.contains(task)) {
				//the synced todo already contains a task with the id.
				Random rand = new Random();
				task = Task.fromDTO(Task.toDTO(task));
				task.setId(Math.abs(rand.nextLong()));
			}
			
			//add the task to todo
			to.add(task);
		} else {
			//The event was deleted after this event. do nothing
		}
		
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
						// TODO Created is not possible in this case
					} else if (event.getAction() == Action.Updated) {
						syncList.add(dbTodo.load(event.getEntityUid(), user));
					} else if (event.getAction() == Action.Deleted) {
						// TODO send a deleted list
					}
				}
			}
		}
		for (HistoryEvent eventDB : listDBTodo) {
			// TODO In this case we have to remember the time of the last sync -
			// is a better solution as to send all lists
			if (eventDB.getAction() == Action.Created) {
				if (getListById(request.getTodos(), eventDB.getEntityUid()) == null) {
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
		dbHistory.save(newEvent, user);
	}
}

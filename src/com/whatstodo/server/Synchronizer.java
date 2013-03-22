package com.whatstodo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.Task;
import com.whatstodo.server.manager.HistoryEventManager;
import com.whatstodo.server.manager.TodoManager;

public class Synchronizer {

	private HistoryEventManager dbHistory;
	private TodoManager dbTodo;

	public Synchronizer() {

		this.dbHistory = HistoryEventManager.getInstance();
		this.dbTodo = TodoManager.getInstance();
	}

	
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

			// Both sides have changes now merge!

			// Merge both histories
			history.addAll(serverHistory);
			Collections.sort(history);

			syncedTodo = merge(user, history, oldTodo, appTodo);

		} else {
			// History is empty so the list on the server is the newest

			ListDTO loaded = dbTodo.load(appTodo.getId(), user);
			if(loaded == null) {
				// list was not on server so just send back the list and save
				loaded = todo;
			}
			syncedTodo = com.whatstodo.models.List.fromDTO(loaded);
		}

		dbTodo.save(com.whatstodo.models.List.toDTO(syncedTodo), user);
		return com.whatstodo.models.List.toDTO(syncedTodo);
	}

	private com.whatstodo.models.List merge(String user,
			List<HistoryEvent> history, com.whatstodo.models.List serverTodo,
			com.whatstodo.models.List appTodo) {
		com.whatstodo.models.List syncedTodo;
		
		//Use the server version as starting point
		//TODO Do a real copy !
		syncedTodo = com.whatstodo.models.List.fromDTO(com.whatstodo.models.List.toDTO(serverTodo));
		
		// Iterate over history starting at oldest entry
		for (HistoryEvent event : history) {

			switch (event.getType()) {
			case Task:
				mergeTask(syncedTodo, serverTodo, appTodo, event);
				break;
			case Todo:
				switch (event.getAction()) {
				case Deleted:
					//This will be handled in the sync all
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
			
			//add the task to todo
			to.add(task);
		} else {
			//The event was deleted after this event. do nothing
		}
		
	}

	public List<ListDTO> synchronizeAll(String user, List<ListDTO> appTodos,
			List<HistoryEvent> history) {
		
		List<ListDTO> synced = null;

		if (history != null && !history.isEmpty()) {
			// Sort to have oldest as first
			Collections.sort(history);

			// Load the old events for the Todo since the last sync
			List<HistoryEvent> todoHistory = dbHistory.load(null, null, 0l,
					null, history.get(0).getTimeOfChange(), null, user);
			
			history.addAll(todoHistory);
			Collections.sort(history);
			
			//Merge
			//Base is the server version
			synced = dbTodo.getAllDTOs(user);
			
			for (HistoryEvent event : history) {
				switch (event.getType()) {
				case Todo:
					
					switch (event.getAction()) {
					case Created:
						if(!event.isSynchronized()) {
							ListDTO todo = getListById(appTodos, event.getEntityUid());
							if(todo != null) {
								synced.add(todo);
							}
						} else {
							//Todo is already in the synced list
						}
						break;
					case Updated:
						if(!event.isSynchronized()) {
							ListDTO todo = getListById(appTodos, event.getEntityUid());
							if(todo != null) {
								synced.remove(todo);
								todo = synchronizeList(user, todo, filterHistory(history, event.getEntityUid(), false));
								synced.add(todo);
							}
						} else {
							//Todo is already in the synced list
						}
						break;
					case Deleted:
						ListDTO dummyList = new ListDTO();
						dummyList.setId(event.getEntityUid());
						synced.remove(dummyList);
						dbTodo.delete(dummyList, user);
						break;
					default:
						break;
					}
					saveDBEvent(event, user);
					break;
				case Task:
					//Tasks will be merged inside the todo
				default:
					break;
				}
			}
			
		} else {
			//Nothing happend at the app. Send back the server version
			
			synced = dbTodo.getAllDTOs(user);
		}

		//Save synced list and send it back
		for (ListDTO todo : synced) {
			dbTodo.save(todo, user);
		}
		
		return synced;
	}

	private ListDTO getListById(List<ListDTO> listDTOs, Long id) {

		for (ListDTO item : listDTOs) {
			if (item.getId() == id)
				return item;
		}
		return null;
	}
	
	private List<HistoryEvent> filterHistory(List<HistoryEvent> history, long todoId, boolean isSynced) {
		
		ArrayList<HistoryEvent> result = new ArrayList<HistoryEvent>();
		for (HistoryEvent event : history) {
			
			if((event.getEntityUid() == todoId || event.getParentEntityUid() == todoId) &&
					event.isSynchronized() == isSynced) {
				
				result.add(event);
			}
		}
		
		return result;
		
	}

	private void saveDBEvent(HistoryEvent newEvent, String user) {
		newEvent.setSynchronized(true);
		dbHistory.save(newEvent, user);
	}
}

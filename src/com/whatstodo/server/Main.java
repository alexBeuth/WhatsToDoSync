package com.whatstodo.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.models.HistoryEvent.Type;
import com.whatstodo.server.manager.HistoryEventManager;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws IllegalArgumentException,
			IOException {

		// HttpServer server = HttpServerFactory
		// .create("http://localhost:8080/rest");
		// server.start();
		// while (true);
		// server.stop(0);

		// MongoDB Test HistoryDAO

		HistoryEvent event = new HistoryEvent();
		event.setAction(Action.Read);
		event.setEntityUid(3);
		event.setId(0);
		event.setType(Type.Task);
		event.setTimeOfChange(Calendar.getInstance().getTime());

		HistoryEvent event1 = new HistoryEvent();
		event1.setAction(Action.Deleted);
		event1.setEntityUid(5);
		event1.setId(1);
		event1.setSynchronized(true);
		event1.setType(Type.Todo);
		event1.setTimeOfChange(Calendar.getInstance().getTime());

		HistoryEventManager mongo = HistoryEventManager.getInstance("Niko");

		List<HistoryEvent> resultList = new ArrayList<HistoryEvent>();

		resultList = mongo.getAllDTOs("Niko");
		System.out.println("Initial:" + resultList.toString());
		for (HistoryEvent item : resultList) {
			System.out.println("Item: " + item.getAction().toString() + " "
					+ item.getEntityUid() + " ID: " + item.getId());
		}

		mongo.deleteAll("Niko");

		resultList = mongo.getAllDTOs("Niko");
		System.out.println("DeleteAll:" + resultList.toString());
		for (HistoryEvent item : resultList) {
			System.out.println("Item: " + item.getAction().toString() + " "
					+ item.getEntityUid() + " ID: " + item.getId());
		}

		mongo.save(event, "Niko");
		mongo.save(event1, "Niko");

		resultList = mongo.getAllDTOs("Niko");
		System.out.println("Add:" + resultList.toString());
		for (HistoryEvent item : resultList) {
			System.out.println("Item: " + item.getAction().toString() + " "
					+ item.getEntityUid() + " ID: " + item.getId());
		}

		HistoryEvent event2 = new HistoryEvent();
		event2 = event1;
		event2.setTimeOfChange(Calendar.getInstance().getTime());

		resultList = mongo.load(event2, "Niko");
		System.out.println("Load:" + resultList.toString());
		for (HistoryEvent item : resultList) {
			System.out.println("Item: " + item.getAction().toString() + " "
					+ item.getEntityUid() + " ID: " + item.getId());
		}

		// Test MongoDB ListDAO

		// TaskDTO task1 = new TaskDTO();
		// task1.setName("task1");
		// task1.setId(3);
		//
		// TaskDTO task2 = new TaskDTO();
		// task2.setName("task2");
		// task2.setId(12);
		//
		// TaskDTO[] test = {task1, task2};
		//
		// ListDTO listDTO = new ListDTO();
		// listDTO.setId(3);
		// listDTO.setName("0 mit id 3");
		// listDTO.setTasks(test);
		//
		// ListDTO listDTO1 = new ListDTO();
		// listDTO1.setId(11);
		// listDTO1.setName("1 mit id 11");
		//
		// ListDTO listDTO2 = new ListDTO();
		// listDTO2.setId(11);
		// listDTO2.setName("2 mit id 11");
		//
		// List<ListDTO> resultList = new ArrayList<ListDTO>();
		//
		// TodoManager mongo = TodoManager.getInstance("Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("Initial:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
		//
		// mongo.deleteAll("Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("1.Delete:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
		//
		// mongo.save(listDTO, "Niko");
		// mongo.save(listDTO1, "Niko");
		// mongo.save(listDTO2, "Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("complete:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
		//
		// mongo.delete(listDTO1, "Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("Delete one:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
		//
		// listDTO1.setName("1 update");
		// mongo.save(listDTO1, "Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("Update:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
		//
		// ListDTO getJson = mongo.load(11, "Niko");
		// System.out.println("Get Json by Id = 11:" + getJson.getName());
		//
		// mongo.save(listDTO2, "Niko");
		//
		// resultList = mongo.getAllDTOs("Niko");
		// System.out.println("Delete one:" + resultList.toString());
		// for (ListDTO item : resultList) {
		// System.out.println("Item: " + item.getName() + " " + item.getId());
		// }
	}

}

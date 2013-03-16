package com.whatstodo.server;

import java.io.IOException;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws IllegalArgumentException,
			IOException {

		HttpServer server = HttpServerFactory
				.create("http://localhost:8080/rest");
		server.start();
		// while (true);
		server.stop(0);

		// MongoDB Test

		// ListDTO listDTO = new ListDTO();
		// listDTO.setId(3);
		// listDTO.setName("0 mit id 3");
		//
		// ListDTO listDTO1 = new ListDTO();
		// listDTO1.setId(11);
		// listDTO1.setName("1 mit id 11");
		//
		// ListDTO listDTO2 = new ListDTO();
		// listDTO1.setId(11);
		// listDTO1.setName("2 mit id 11");
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

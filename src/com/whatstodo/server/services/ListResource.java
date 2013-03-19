package com.whatstodo.server.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.whatstodo.dtos.ListDTO;
import com.whatstodo.net.request.SyncAllRequest;
import com.whatstodo.net.request.SyncTodoRequest;
import com.whatstodo.server.manager.TodoManager;

@Path("list")
public class ListResource {

	@GET
	@Path("serverinfo")
	@Produces(MediaType.APPLICATION_JSON)
	public ServerInfo serverinfo() {
		ServerInfo info = new ServerInfo();
		info.server = System.getProperty("os.name") + " "
				+ System.getProperty("os.version");
		return info;
	}

	@GET
	@Path("{user}/{listid}")
	@Produces(MediaType.APPLICATION_JSON)
	public ListDTO getList(@PathParam("user") String userUID,
			@PathParam("listid") long listId) {

		return TodoManager.getInstance().load(listId, userUID);
	}

	@POST
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ListDTO updateList(@PathParam("user") String userUID,
			@PathParam("listid") long listId, ListDTO message) {

		System.out.println("Update List for " + userUID + " with ListID " + listId);
		return TodoManager.getInstance().save(message, userUID);
	}

	@PUT
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public ListDTO saveList(@PathParam("user") String userUID,
			@PathParam("listid") long listId, ListDTO message) {

		System.out.println("Save List for " + userUID + " with ListID " + listId);
		return TodoManager.getInstance().save(message, userUID);
	}

	@DELETE
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteList(@PathParam("user") String userUID,
			@PathParam("listid") long listId) {

		System.out
				.println("Delete List for " + userUID + " with ListID " + listId);
		ListDTO toDelete = new ListDTO();
		toDelete.setId(listId);
		TodoManager.getInstance().delete(toDelete, userUID);
	}
	
	@POST
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ListDTO synchronizeList(@PathParam("user") String userUID,
			@PathParam("listid") long listId, SyncTodoRequest request) {

		System.out.println("Sync List for " + userUID + " with ListID " + listId);
		
		//TODO Sync here !
		return request.getTodo();
	}
	
	@POST
	@Path("{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ListDTO> synchronizeAllTodos(@PathParam("user") String user,
			SyncAllRequest request) {

		System.out.println("Sync Lists for " + user);
		
		//TODO Sync here !
		return request.getTodos();
	}
}

class ServerInfo {
	public String server;
}

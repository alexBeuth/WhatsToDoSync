package com.whatstodo.server.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.whatstodo.models.List;

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
	public List saveList(@PathParam("user") String user,  @PathParam("listid") long listId) {
		
		System.out.println("Get List for " + user + " with ListID " + listId);
		return null;
	}

	@POST
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List synchronizeList(@PathParam("user") String user,  @PathParam("listid") long listId, List message) {
		
		System.out.println("Sync List for " + user + " with ListID " + listId);
		return message;
	}
	
	@PUT
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveList(@PathParam("user") String user,  @PathParam("listid") long listId, List message) {
		
		System.out.println("Save List for " + user + " with ListID " + listId);
	}
	
	@DELETE
	@Path("{user}/{listid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteList(@PathParam("user") String user,  @PathParam("listid") long listId) {
		
		System.out.println("Delete List for " + user + " with ListID " + listId);
	}
}

class ServerInfo {
	public String server;
}

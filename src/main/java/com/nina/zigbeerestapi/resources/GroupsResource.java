package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Group;
import com.nina.zigbeerestapi.core.Groups;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.codahale.metrics.annotation.Timed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupsResource {
	private final SerialCommunication serialComm;
	private final Groups groups;

	public GroupsResource(SerialCommunication serialComm, Groups groups) {
		this.serialComm = serialComm;
		this.groups = groups;
	}

	@GET
	@Timed
	public ArrayList<String> getAllGroups() {
		return groups.getAllGroups();
	}
}
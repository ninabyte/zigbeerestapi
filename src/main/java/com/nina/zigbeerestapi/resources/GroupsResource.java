package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Group;
import com.nina.zigbeerestapi.core.Groups;
import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupsResource {
	private final SerialCommunication serialComm;
	private final Groups groups;
	private final Lights lights;
	private final AtomicLong counter;
	private final static String DUMMY_ENDPOINT = "0x01"; //could be anything 1-240

	public GroupsResource(SerialCommunication serialComm, Groups groups, 
			Lights lights) {
		this.serialComm = serialComm;
		this.groups = groups;
		this.lights = lights;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public ArrayList<String> getAllGroups() {
		return groups.getAllGroups();
	}

	@POST
	@Timed
	public Group createNewGroup(
			@QueryParam("name") Optional<String> name) {
		long id = counter.incrementAndGet();
		Group group = new Group(id, "Group " + id);
		
		if(name.isPresent()) {
			group.setName(name.get());
		}

		groups.add(group);
		return group;
	}

	@PUT
	@Timed
	@Path("{id}")
	public Group editGroup(@PathParam("id") long id, 
			@QueryParam("name") Optional<String> name, 
			@QueryParam("lights") List<Long> lights) {

		Group group = groups.getGroup(id);
		//kalo group null, return error here

		if(name.isPresent()) {
			group.setName(name.get());
		}

		if(!lights.isEmpty()) {
			addLightsToGroup(group, lights);	
		}
		return group;
	}

	private void addLightsToGroup(Group group, List<Long> lightIds) {
		for(Long lightId: lightIds) {
			Light light = lights.getLightById(lightId.longValue());
			if (light !=null) {
				//kirim addGroup ke serial, kalo sukses:
				group.addLight(lightId);
			}
		}
	}
}
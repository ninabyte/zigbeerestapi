package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Group;
import com.nina.zigbeerestapi.core.Groups;
import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.core.Config;
import com.nina.zigbeerestapi.core.Constants;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupsResource {
	private final SerialCommunication serialComm;
	private final Groups groups;
	private final Lights lights;
	private final AtomicLong counter;

	public GroupsResource(SerialCommunication serialComm, Groups groups, 
			Lights lights) {
		this.serialComm = serialComm;
		this.groups = groups;
		this.lights = lights;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public List<String> getAllGroups() {
		return groups.getAllGroups();
	}

	@GET
	@Timed
	@Path("{id}")
	public Group getGroup(@PathParam("id") long id) {
		Group group = groups.getGroupById(id);
		if (group == null) {
			throw new WebApplicationException(404);
		}
		return group;
	}

	@POST
	@Timed
	public Group createNewGroup(@QueryParam("name") Optional<String> name, 
			@QueryParam("lights") List<Long> lights) {
		long id = counter.incrementAndGet();
		Group group = new Group(id, "Group " + id);
		groups.add(group);

		if(name.isPresent()) {
			group.setName(name.get());
		}

		if(!lights.isEmpty()) {
			addLightsToGroup(group, lights);
		}

		return group;
	}

	@PUT
	@Timed
	@Path("{id}")
	public Group editGroup(@PathParam("id") long id, 
			@QueryParam("name") Optional<String> name, 
			@QueryParam("lights") List<Long> lights) {

		Group group = groups.getGroupById(id);
		if (group == null) {
			throw new WebApplicationException(404);
		}

		if(name.isPresent()) {
			group.setName(name.get());
		}

		if(!lights.isEmpty()) {
			addLightsToGroup(group, lights);	
		}
		return group;
	}

	@PUT
	@Timed
	@Path("{id}/state")
	public Group editGroupState(@PathParam("id") long id,
			@QueryParam("on") Optional<Boolean> on, 
			@QueryParam("brightness") Optional<Integer> brightness,
			@QueryParam("transitiontime") Optional<Integer> transitionTime) {

		Group group = groups.getGroupById(id);
		if (group == null) {
			throw new WebApplicationException(404);
		}

		Integer transitionTimeInt = Config.DEFAULT_TRANSITION_TIME;
		if(transitionTime.isPresent()) {
			transitionTimeInt = transitionTime.get();
		}

		if(on.isPresent()) {
			long prevOnLastUpdated = group.getLatestStateOnLastUpdated();

		    String value = "-off";
			if (on.get()) {
				value = "-on";
			}
			serialComm.writeCommand("onOff -g " + group.getGroupAddress() 
				+ " " + Constants.DUMMY_ENDPOINT + " " + value);

			List<Long> lightIds = group.getLights();
			for(Long lightId: lightIds) {
				
				Light light = lights.getLightById(lightId.longValue());

				try {
				   	Thread.sleep(Config.READ_ATTR_DELAY_MS);
				}
				catch ( InterruptedException e ) {
					e.printStackTrace();
				}

				serialComm.writeCommand("readOnOffAttr -s " 
					+ light.getShortNwkAddress() + " " + light.getEndpointId() 
					+ " " + Constants.ONOFF_CLUSTER_ONOFF_ATTRIB_ID);
				
				long timeout = System.currentTimeMillis() 
						+ Config.WAIT_ATTR_TIMEOUT_MS;
				while(light.getOnLastUpdated() <= prevOnLastUpdated) {
					if (System.currentTimeMillis() >= timeout) break;
				}
			}

			group.setLatestStateOn(on.get().booleanValue());

		}

		if(brightness.isPresent()) {
			long prevBrightnessLastUpdated = 
					group.getLatestStateBrightnessLastUpdated();

			Integer value = brightness.get();
			if (value.compareTo(Constants.MAX_BRIGHTNESS) > 0) {
				value = Constants.MAX_BRIGHTNESS;
			}
			serialComm.writeCommand("moveToLevel -g " 
				+ group.getGroupAddress() + " " + Constants.DUMMY_ENDPOINT 
				+ " " + value + " " + transitionTimeInt + " " + 1);

			List<Long> lightIds = group.getLights();
			for(Long lightId: lightIds) {
				Light light = lights.getLightById(lightId.longValue());

				try {
				   	Thread.sleep(Config.READ_ATTR_DELAY_MS);
				}
				catch ( InterruptedException e ) {
					e.printStackTrace();
				}

				serialComm.writeCommand("readLevelAttr -s " 
					+ light.getShortNwkAddress() + " " + light.getEndpointId() 
					+ " " + Constants.LEVEL_CLUSTER_LEVEL_ATTRIB_ID);
				
				long timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
				while(light.getBrightnessLastUpdated() <= prevBrightnessLastUpdated) {
					if (System.currentTimeMillis() >= timeout) break;
				}
			}

			group.setLatestStateBrightness(value);
		}

		return group;
	}

	@DELETE
	@Timed
	@Path("{id}")
	public List<String> deleteGroup(@PathParam("id") long id) {
		Group group = groups.getGroupById(id);
		if (group == null) {
			throw new WebApplicationException(404);
		}

		List<Long> lightIds = group.getLights();
		int lightAmt = lightIds.size();
		for(int i=0; i<lightAmt; i++) {
			long lightId = lightIds.get(i);
			removeLightFromGroup(group, lightId);
		}

		groups.remove(id);
		return groups.getAllGroups();
	} 

	@DELETE
	@Timed
	@Path("{id}/lights")
	public Group deleteGroup(@PathParam("id") long id,
			@QueryParam("lights") List<Long> lights) {
		Group group = groups.getGroupById(id);
		if (group == null) {
			throw new WebApplicationException(404);
		}

		for(long lightId: lights) {
			removeLightFromGroup(group, lightId);
		}

		return group;
	} 

	private void addLightsToGroup(Group group, List<Long> lightIds) {
		for(Long lightId: lightIds) {
			Light light = lights.getLightById(lightId.longValue());
			if (light !=null) {
				long prevLightsLastUpdated = group.getLightsLastUpdated();

				serialComm.writeCommand("addGroup -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " " + group.getId());

				long timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
				while(group.getLightsLastUpdated() <= prevLightsLastUpdated) {
					if (System.currentTimeMillis() >= timeout) break;
				}
			}
		}
	}

	private void removeLightFromGroup(Group group, long lightId) {
		Light light = lights.getLightById(lightId);
		if (light != null) {
			long prevLightsLastUpdated = group.getLightsLastUpdated();

			serialComm.writeCommand("removeGroup -s " + light.getShortNwkAddress() 
			+ " " + light.getEndpointId() + " " + group.getId());

			long timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
			while(group.getLightsLastUpdated() <= prevLightsLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}
	}
}
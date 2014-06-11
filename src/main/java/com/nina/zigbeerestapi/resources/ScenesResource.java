package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Config;
import com.nina.zigbeerestapi.core.Constants;
import com.nina.zigbeerestapi.core.Scene;
import com.nina.zigbeerestapi.core.Scenes;
import com.nina.zigbeerestapi.core.Group;
import com.nina.zigbeerestapi.core.Groups;
import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.core.State;
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

@Path("/groups/{groupId}/scenes")
@Produces(MediaType.APPLICATION_JSON)
public class ScenesResource {
	private final SerialCommunication serialComm;
	private final Lights lights;
	private final Groups groups;

	public ScenesResource(SerialCommunication serialComm, Groups groups, 
			Lights lights) {
		this.serialComm = serialComm;
		this.groups = groups;
		this.lights = lights;
	}

	@GET
	@Timed
	@Path("{sceneId}")
	public Scene getScene(@PathParam("groupId") long groupId,
			@PathParam("sceneId") long sceneId) {
		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}
		
		Scene scene = group.getScenes().getSceneById(sceneId);
		if(scene == null) {
			throw new WebApplicationException(404);
		}

		return scene;
	}

	@POST
	@Timed
	public Scene createNewScene(@PathParam("groupId") long groupId,
		@QueryParam("name") Optional<String> name) {

		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}

		Scenes scenes = group.getScenes();
		Scene newScene = scenes.createNewScene();

		if(name.isPresent()) {
			newScene.setName(name.get());
		}

		List<Long> lightIds = group.getLights();
		for(Long lightId: lightIds) {

			long prevStateLastUpdated = newScene.getStateLastUpdated();
			Light light = lights.getLightById(lightId);
			serialComm.writeCommand("storeScene -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + group.getGroupAddress() + " " + newScene.getId());

			long timeout = System.currentTimeMillis() 
					+ Config.WAIT_ATTR_TIMEOUT_MS;
			while(newScene.getStateLastUpdated() <= prevStateLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}
		
		return newScene;
	}

	@PUT
	@Timed
	@Path("{sceneId}")
	public Scene editScene(@PathParam("groupId") long groupId,
			@PathParam("sceneId") long sceneId,
			@QueryParam("name") Optional<String> name) {

		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}
		
		Scene scene = group.getScenes().getSceneById(sceneId);
		if(scene == null) {
			throw new WebApplicationException(404);
		}

		if(name.isPresent()) {
			scene.setName(name.get());
		}

		return scene;
	}

	@PUT
	@Timed
	@Path("{sceneId}/store")
	public Scene storeSceneStates(@PathParam("groupId") long groupId,
			@PathParam("sceneId") long sceneId) {

		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}
		
		Scene scene = group.getScenes().getSceneById(sceneId);
		if(scene == null) {
			throw new WebApplicationException(404);
		}

		List<Long> lightIds = group.getLights();
		for(Long lightId: lightIds) {

			long prevStateLastUpdated = scene.getStateLastUpdated();
			Light light = lights.getLightById(lightId);
			serialComm.writeCommand("storeScene -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + group.getGroupAddress() + " " + scene.getId());

			long timeout = System.currentTimeMillis() 
					+ Config.WAIT_ATTR_TIMEOUT_MS;
			while(scene.getStateLastUpdated() <= prevStateLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}

		return scene;	
	}

	@PUT
	@Timed
	@Path("{sceneId}/recall")
	public void recallSceneStates(@PathParam("groupId") long groupId,
			@PathParam("sceneId") long sceneId) {

		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}
		
		Scene scene = group.getScenes().getSceneById(sceneId);
		if(scene == null) {
			throw new WebApplicationException(404);
		}

		serialComm.writeCommand("recallScene -g " + group.getGroupAddress() 
			+ " " + Constants.DUMMY_ENDPOINT + " " + group.getGroupAddress()
			+ " " + scene.getId());
	}

	@DELETE
	@Timed
	@Path("{sceneId}")
	public Group deleteScene(@PathParam("groupId") long groupId,
			@PathParam("sceneId") long sceneId) {

		Group group = groups.getGroupById(groupId);
		if(group == null) {
			throw new WebApplicationException(404);
		}
		
		Scenes scenes = group.getScenes();
		Scene scene = scenes.getSceneById(sceneId);
		if(scene == null) {
			throw new WebApplicationException(404);
		}

		List<Long> lightIds = group.getLights();
		for(Long lightId: lightIds) {

			long prevScenesLastDeleted = scenes.getScenesLastDeleted();
			Light light = lights.getLightById(lightId);
			serialComm.writeCommand("removeScene -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + group.getGroupAddress() + " " + scene.getId());

			long timeout = System.currentTimeMillis() 
					+ Config.WAIT_ATTR_TIMEOUT_MS;
			while(scenes.getScenesLastDeleted() <= prevScenesLastDeleted) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}
		return group;
	}
}
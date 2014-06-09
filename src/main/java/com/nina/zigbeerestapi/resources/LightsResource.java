package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.core.Config;
import com.nina.zigbeerestapi.core.Constants;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/lights")
@Produces(MediaType.APPLICATION_JSON)
public class LightsResource {

    private final SerialCommunication serialComm;
	private final Lights lights;

	public LightsResource(SerialCommunication serialComm, Lights lights) {
		this.serialComm = serialComm;
		this.lights = lights;
	}

	@GET
	@Timed
	public List<String> getAllLights() {
		return lights.getAllLights();
	}

	@GET
	@Timed
	@Path("{id}")
	public Light getLight(@PathParam("id") long id) {
		Light light = lights.getLightById(id);
		if (light == null) {
			throw new WebApplicationException(404);
		}

		long prevStackVersionLastUpdated = light.getStackVersionLastUpdated();
		
		serialComm.writeCommand("readBasicAttr -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " " 
				+ Constants.BASIC_CLUSTER_STACK_VERSION_ATTRIB_ID);

		long timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
		while(light.getStackVersionLastUpdated() <= prevStackVersionLastUpdated) {
			if (System.currentTimeMillis() >= timeout) break;
		}

		long prevModelIdLastUpdated = light.getModelIdLastUpdated();
		
		serialComm.writeCommand("readBasicAttr -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " " 
				+ Constants.BASIC_CLUSTER_MODEL_ID_ATTRIB_ID);

		timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
		while(light.getModelIdLastUpdated() <= prevModelIdLastUpdated) {
			if (System.currentTimeMillis() >= timeout) break;
		}

		return light;
	}

	@PUT
	@Timed
	@Path("{id}")
	public Light editLight(@PathParam("id") long id, 
			@QueryParam("name") Optional<String> name) {

		Light light = lights.getLightById(id);
		if (light == null) {
			throw new WebApplicationException(404);
		}

		if(name.isPresent()) {
			light.setName(name.get());
		}

		return light;
	}

	@PUT
	@Timed
	@Path("state/{id}")
	public Light setState (@PathParam("id") long id, 
			@QueryParam("on") Optional<Boolean> on, 
			@QueryParam("brightness") Optional<Integer> brightness,
			@QueryParam("transitiontime") Optional<Integer> transitionTime) {

		Light light = lights.getLightById(id);
		if (light == null) {
			throw new WebApplicationException(404);
		}

		Integer transitionTimeInt = Config.DEFAULT_TRANSITION_TIME;
		if(transitionTime.isPresent()) {
			transitionTimeInt = transitionTime.get();
		}

		if(on.isPresent()) {
			long prevOnLastUpdated = light.getOnLastUpdated();

		    String value = "-off";
			if (on.get()) {
				value = "-on";
			}
			serialComm.writeCommand("onOff -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " " + value);

			try {
			   	Thread.sleep(Config.READ_ATTR_DELAY_MS);
			}
			catch ( InterruptedException e ) {
				e.printStackTrace();
			}

			serialComm.writeCommand("readOnOffAttr -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + Constants.ONOFF_CLUSTER_ONOFF_ATTRIB_ID);
			
			long timeout = System.currentTimeMillis() + Config.WAIT_ATTR_TIMEOUT_MS;
			while(light.getOnLastUpdated() <= prevOnLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}

		if(brightness.isPresent()) {
			long prevBrightnessLastUpdated = light.getBrightnessLastUpdated();

			Integer value = brightness.get();
			if (value.compareTo(Constants.MAX_BRIGHTNESS) > 0) {
				value = Constants.MAX_BRIGHTNESS;
			}
			serialComm.writeCommand("moveToLevel -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + value + " " + transitionTimeInt + " " + 1);

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

		return light;
	}	

}
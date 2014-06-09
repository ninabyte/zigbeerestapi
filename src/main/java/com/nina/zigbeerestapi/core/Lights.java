package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Lights {
	
	private List<Light> lights;
	private final AtomicLong counter;

	public Lights() {
		lights = new ArrayList<Light>();
		counter = new AtomicLong();
	}

	@JsonProperty
	public List<String> getAllLights() {
		List<String> arrayId = new ArrayList<String>();
		for (Light light : lights){
			arrayId.add("" + light.getId());
		}
		return arrayId;
	}

	@JsonProperty
	public Light getLightById(long id) {
		for (Light light: lights){
			if (light.getId() == id) {
				return light;
			}
		}
		return null;
	}

	@JsonProperty
	public Light getLightByAddressAndEndpoint(String addr, String endpoint) {
		for (Light light: lights){
			if (light.getShortNwkAddress().equals(addr) 
					&& light.getEndpointId().equals(endpoint)) {
				return light;
			}
		}
		return null;
	}

	public Light createLight() {
		long id = counter.incrementAndGet();
		return createLight(id, "Light " + id);
	}

	public Light createLight(String name) {
		return createLight(counter.incrementAndGet(), name);
	}


	public Light createLight(long id, String name) {
		Light newLight = new Light(id);
		newLight.setName(name);
		lights.add(newLight);
		return newLight;
	}
}
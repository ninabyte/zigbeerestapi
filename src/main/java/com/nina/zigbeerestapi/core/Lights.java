package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Lights {
	
	private ArrayList<Light> lights;
	private final AtomicLong counter;

	public Lights() {
		lights = new ArrayList<Light>();
		counter = new AtomicLong();
	}

	@JsonProperty
	public ArrayList<String> getAllLights() {
		ArrayList<String> arrayId = new ArrayList<String>();
		for (Light light : lights){
			arrayId.add("" + light.getId());
		}

		return arrayId;
	}

	@JsonProperty
	public Light getLight(long id) {
		for (Light light: lights){
			if (light.getId() == id) {
				return light;
			}
		}

		return null;
	}

	@JsonProperty
	public Light getLightByAddress(String addr) {
		for (Light light: lights){
			if (light.getShortNwkAddress().equals(addr)) {
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
	
	/*
	private class LightIdentifier {
		@JsonProperty
		long id;

		@JsonProperty
		String name;
	}
	*/
	
}
package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import java.util.HashMap;

public class Scene {

	@JsonProperty
	private long id;

	@JsonProperty
	private String name;
	
	@JsonProperty
	private Map<Long, State> storedStates;

	public Scene (long id, String name) {
		this.id = id;
		this.name = name;
		storedStates = new HashMap<Long, State>();
	}



}
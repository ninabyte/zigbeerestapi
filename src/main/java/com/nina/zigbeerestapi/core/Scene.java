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
	private Map<Long, State> savedStates;

    @JsonIgnore
    private long stateLastUpdated = 0L;

	public Scene (long id, String name) {
		this.id = id;
		this.name = name;
		savedStates = new HashMap<Long, State>();
	}

	public long getId() {
        return id;
    }

    public String getName() {
    	return name;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public void addSavedState(Long lightId, State lightState) {
    	savedStates.put(lightId, lightState);
        stateLastUpdated = System.currentTimeMillis();
    }

    public State getSavedState(Long lightId) {
    	return savedStates.get(lightId);
    }

    public long getStateLastUpdated() {
        return stateLastUpdated;
    }
}
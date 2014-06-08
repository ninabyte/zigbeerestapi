package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class Group {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private boolean on;
    
	@JsonProperty
    private long onLastUpdated = 0L;

    @JsonProperty
    private ArrayList<Long> lights;

	public Group(long id, String name) {
		this.id = id;
		this.name = name;
		lights = new ArrayList<Long>();
	}

	public long getId() {
		return id;
	}

	public String getGroupAddress() {
		return ""+id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addLight(Long lightId) {
		if (!lights.contains(lightId)) {
			lights.add(lightId);
		}
	}

	public boolean removeLight(String lightId) {
		return lights.remove(lightId);
	}

}
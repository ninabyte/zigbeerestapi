package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Group {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String groupAddress;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private boolean on;
    
	@JsonProperty
    private long onLastUpdated = 0L;

	public Group(long id) {
		this(id, "Group " + id);
	}

	public Group(long id, String name) {
		this.id = id;
		this.name = name;
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

}
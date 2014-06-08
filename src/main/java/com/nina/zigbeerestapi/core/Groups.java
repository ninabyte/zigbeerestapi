package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Groups {

	private ArrayList<Group> groups;
	private final AtomicLong counter;

	public Groups() {
		groups = new ArrayList<Group>();
		counter = new AtomicLong();
	}

	@JsonProperty
	public ArrayList<String> getAllGroups() {
		ArrayList<String> arrayId = new ArrayList<String>();
		for (Group group : groups){
			arrayId.add("" + group.getId());
		}

		return arrayId;
	}

}
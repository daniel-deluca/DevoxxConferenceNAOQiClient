/* Generated by JavaFromJSON */
/*http://javafromjson.dashingrocket.com*/

package eu.delucaconsulting.robot.rest.domain.voting;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Votes {
	@JsonProperty("talks")
	private Talk[] talks;

 	public void setTalks(Talk[] talks) {
		this.talks = talks;
	}

	public Talk[] getTalks() {
		return talks;
	}

}
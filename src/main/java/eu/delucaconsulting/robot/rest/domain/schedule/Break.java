/* Generated by JavaFromJSON */
/*http://javafromjson.dashingrocket.com*/

package eu.delucaconsulting.robot.rest.domain.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Break {
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Break [nameen=").append(nameen).append(", id=").append(id).append(", namefr=").append(namefr)
				.append(", room=").append(room).append("]");
		return builder.toString();
	}

	@JsonProperty("nameEN")
	private java.lang.String nameen;

 	public void setNameen(java.lang.String nameen) {
		this.nameen = nameen;
	}

	public java.lang.String getNameen() {
		return nameen;
	}

	@JsonProperty("id")
	private java.lang.String id;

 	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getId() {
		return id;
	}

	@JsonProperty("nameFR")
	private java.lang.String namefr;

 	public void setNamefr(java.lang.String namefr) {
		this.namefr = namefr;
	}

	public java.lang.String getNamefr() {
		return namefr;
	}

	@JsonProperty("room")
	private Room room;

 	public void setRoom(Room room) {
		this.room = room;
	}

	public Room getRoom() {
		return room;
	}

}
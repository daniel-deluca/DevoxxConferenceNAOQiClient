/* Generated by JavaFromJSON */
/*http://javafromjson.dashingrocket.com*/

package eu.delucaconsulting.robot.rest.domain.schedule;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Talk {
	@JsonProperty("track")
	private java.lang.String track;

 	public void setTrack(java.lang.String track) {
		this.track = track;
	}

	public java.lang.String getTrack() {
		return track;
	}

	@JsonProperty("summaryAsHtml")
	private java.lang.String summaryashtml;

 	public void setSummaryashtml(java.lang.String summaryashtml) {
		this.summaryashtml = summaryashtml;
	}

	public java.lang.String getSummaryashtml() {
		return summaryashtml;
	}

	@JsonProperty("lang")
	private java.lang.String lang;

 	public void setLang(java.lang.String lang) {
		this.lang = lang;
	}

	public java.lang.String getLang() {
		return lang;
	}

	@JsonProperty("id")
	private java.lang.String id;

 	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getId() {
		return id;
	}

	@JsonProperty("summary")
	private java.lang.String summary;

 	public void setSummary(java.lang.String summary) {
		this.summary = summary;
	}

	public java.lang.String getSummary() {
		return summary;
	}

	@JsonProperty("trackId")
	private java.lang.String trackid;

 	public void setTrackid(java.lang.String trackid) {
		this.trackid = trackid;
	}

	public java.lang.String getTrackid() {
		return trackid;
	}

	@JsonProperty("title")
	private java.lang.String title;

 	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Talk [track=").append(track).append(", summaryashtml=").append(summaryashtml).append(", lang=")
				.append(lang).append(", id=").append(id).append(", summary=").append(summary).append(", trackid=")
				.append(trackid).append(", title=").append(title).append(", speakers=")
				.append(Arrays.toString(speakers)).append(", talktype=").append(talktype).append("]");
		return builder.toString();
	}

	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	public java.lang.String getTitle() {
		return title;
	}

	@JsonProperty("speakers")
	private Speaker[] speakers;

 	public void setSpeakers(Speaker[] speakers) {
		this.speakers = speakers;
	}

	public Speaker[] getSpeakers() {
		return speakers;
	}

	@JsonProperty("talkType")
	private java.lang.String talktype;

 	public void setTalktype(java.lang.String talktype) {
		this.talktype = talktype;
	}

	public java.lang.String getTalktype() {
		return talktype;
	}

}
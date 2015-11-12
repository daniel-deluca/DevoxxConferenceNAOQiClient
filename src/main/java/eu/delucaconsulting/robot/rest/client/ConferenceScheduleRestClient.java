/**
 * 
 */
package eu.delucaconsulting.robot.rest.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.delucaconsulting.robot.rest.domain.schedule.Slot;
import eu.delucaconsulting.robot.rest.domain.schedule.Slots;
import eu.delucaconsulting.robot.rest.domain.schedule.Speaker;
import eu.delucaconsulting.robot.rest.domain.schedule.Talk;


/**
 * @author danieldeluca
 *
 */
public class ConferenceScheduleRestClient {
	private static final Logger logger = LogManager.getLogger(ConferenceScheduleRestClient.class.getName());

	// Schedule API documentation http://cfp.devoxx.be/api
	private static final String CONFERENCE_HOST = "http://cfp.devoxx.be";
	private static final String CONFERENCE_PATH_SCHEDULES = "api/conferences/DV15/schedules";

	private Client restClient = null;
	private WebTarget webTarget = null;

	public static void main(String[] args) throws Exception {
		ConferenceScheduleRestClient client = new ConferenceScheduleRestClient();
		client.getCurrentSessions();
	}

	public ConferenceScheduleRestClient() {
		restClient = ClientBuilder.newClient();
	}

	public String getCurrentSessions() {
		logger.info("Getting Current Sessions");
		Date date = new Date();
		StringBuffer result = new StringBuffer("Right now, ");
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
		String dayOfWeek = dateFormat.format(date);
		String restPath = CONFERENCE_PATH_SCHEDULES + "/" + dayOfWeek.toLowerCase();
		webTarget = restClient.target(CONFERENCE_HOST).path(restPath);
		logger.info("Rest Path:" + restPath + ":");

		Builder builder = webTarget.request();
		String jsonInString = builder.get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		try {
			logger.info(date);
			long currentTime = date.getTime();
			logger.info("Current time ms:" + currentTime + ":");
			Slots slots = mapper.readValue(jsonInString, Slots.class);
			if (slots.getSlots().length > 1) {
				result.append("the ongoing sessions are.         ");
				for (Slot slot : slots.getSlots()) {
					if ((slot.getFromtimemillis().longValue() <= currentTime)
							&& (currentTime <= slot.getTotimemillis().longValue())) {
						Talk talk = slot.getTalk();
						if (talk != null) {
							logger.info("Talk Title:" + talk.getTitle());
							result.append("In " + slot.getRoomname() + ", we have a " + talk.getTalktype()
									+ " about           " + talk.getTitle() + ".              ");
							result.append(".   This session is given by .           ");
							int index = 0;
							int nbrSpeakers = talk.getSpeakers().length;
							for (Speaker speaker : talk.getSpeakers()) {
								index++;
								result.append(speaker.getName() + " ");
								if (index < nbrSpeakers) {
									result.append(" and ");
								}
								logger.info("Speaker:" + speaker.getName());
							}
							result.append(".");
						}
						;
						logger.info("Current Session:" + slot.getRoomname() + ":" + slot.getFromtime() + ":"
								+ slot.getFromtimemillis() + ":" + slot.getTotime() + ":" + slot.getTotimemillis()
								+ ":");
						result.append(". ");
					}
				}
			} else {
				result.append("there are no sessions, please enjoy the exibition floor!");
			}
		} catch (JsonParseException e) {
			logger.fatal(e);
		} catch (JsonMappingException e) {
			logger.fatal(e);
		} catch (IOException e) {
			logger.fatal(e);
		}
		logger.info(result.toString());
		return result.toString();
	}

}

/**
 * 
 */
package eu.delucaconsulting.robot.rest.client;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.delucaconsulting.robot.rest.domain.voting.Talk;
import eu.delucaconsulting.robot.rest.domain.voting.Votes;

/**
 * Voting Server REST client
 * @author danieldeluca
 *
 */
public class ConferenceVotingRestClient {
	private static final Logger logger = LogManager.getLogger(ConferenceVotingRestClient.class.getName());


	// Voting API Documentation : https://bitbucket.org/jonmort/devoxx-vote-api
	private static final String CONFERENCE_VOTING_HOST = "https://api-voting.devoxx.com";
	// DV15 indicates: Devoxx Belgium 2015
	private static final String CONFERENCE_PATH_VOTING = "/DV15/top/talks";

	private Client restClient = null;
	private WebTarget webTarget = null;
	
	private String topConfTalkName = null;
	private String topDayTalkName = null;

	// Run it without the need of a robot
	public static void main(String[] args) throws Exception {
		ConferenceVotingRestClient client = new ConferenceVotingRestClient();
		client.getTopTalks(5, false);
		client.getTopTalks(5, true);
		client.hasTopTalkChanged(true);
		client.hasTopTalkChanged(false);
		client.hasTopTalkChanged(true);
		client.hasTopTalkChanged(false);		
	}

	public ConferenceVotingRestClient() {
		restClient = ClientBuilder.newClient();
	}
	
	/**
	 * Check if the TOP Talk has changed
	 * @param today : indicates if we are dealing with today's top talk or entire conference top talks
	 * @return Text (english) to be said by the robot if top talk changed
	 */
	public String hasTopTalkChanged(boolean today){
		logger.info("Getting getTopTalk");
		String todayWord = "";
		String topTalk = null;
		if (today){
			todayWord = "Today, ";
			topTalk = topDayTalkName;
		}
		else {
			topTalk = topConfTalkName;
		}
		StringBuffer result = new StringBuffer(todayWord + " The top conference talk ");
		
		// Getting the TOP talk (1)
		Votes votes = getRawVotes(1, today);
		// If we do have votes
		if (votes != null){
			Talk currentTopTalk = votes.getTalks()[0];
			String currentTopTalkName = currentTopTalk.getName();
			if (! StringUtils.equalsIgnoreCase(currentTopTalkName, topTalk)){
				if (topTalk == null){
					result.append(" is current.");
				}
				else {
					result.append(" has just changed. "+todayWord+" Current top talk is now.");					
				}
				result.append(currentTopTalk.getTitle() + ", by ");
				int index = 0;
				int nbrSpeakers = currentTopTalk.getSpeakers().length;
				
				// Talk's speakers details
				for (String speaker : currentTopTalk.getSpeakers()) {
					index++;
					result.append(StringUtils.trim(speaker));
					if (index < nbrSpeakers) {
						result.append(" and ");
					}
					logger.info("Speaker:" + speaker + ":");
				}
				// Reformating the average format (only 2 decimals)
				DecimalFormat decimalFormat =  new DecimalFormat("#,##0.##");
				result.append(",  with an average of " + decimalFormat.format(Double.valueOf(currentTopTalk.getAvg()).doubleValue())+ ".");
				if (today){
					topDayTalkName = currentTopTalkName;
				}
				else {
					topConfTalkName = currentTopTalkName;
				}
			}
			else {
				logger.info(todayWord+" Current top talk is still the same :"+topTalk+":");
				return null;
			}
		}
		return result.toString();
		
	}
	
	/**
	 * Get the raw set of votes
	 * @param limit : limit the results to the top limit
	 * @param today : top talk of today or the entire conference
	 * @return the list of votes
	 */
	private Votes getRawVotes(int limit, boolean today){
		Votes votes = null;
		String restPath = CONFERENCE_PATH_VOTING;
		if (today){
			// Just query the VOTING REST api for today top talks
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
			String dayOfWeek = dateFormat.format(date);
			webTarget = restClient.target(CONFERENCE_VOTING_HOST).path(restPath).queryParam("limit", limit).queryParam("day", dayOfWeek.toLowerCase());
		}
		else {
			// Query the VOTING REST api for the entire conference TOP talks			
			webTarget = restClient.target(CONFERENCE_VOTING_HOST).path(restPath).queryParam("limit", limit);
		}
		logger.info(webTarget.getUri().toString());
		Builder builder = webTarget.request();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = builder.get(String.class);
			votes = mapper.readValue(jsonInString, Votes.class);
		} catch (JsonParseException e) {
			logger.fatal(e);
		} catch (JsonMappingException e) {
			logger.fatal(e);
		} catch (IOException e) {
			logger.fatal(e);
		} catch (Exception e) {
			logger.fatal(e);
		}
		return votes;
	}

	/**
	 * Get the TOP talks
	 * @param limit: limit to the top limit
	 * @param today: top talks of today or top talks of the entire conference
	 * @return the text to be said (in English) by the robot
	 */
	public String getTopTalks(int limit, boolean today) {
		String todayWord = "";
		if (today){
			todayWord = "Today, ";
		}

		logger.info(todayWord+" getTopTalks limit:" + limit + ":");
		StringBuffer result = new StringBuffer();
		if (today){
			result.append("Right now, the top " + limit + " talks of today are.");
		}
		else {
			result.append("Right now, the top " + limit + " talks of the entire conference are.");
		}

			Votes votes = getRawVotes(limit, today);
			if (votes != null){
			DecimalFormat decimalFormat =  new DecimalFormat("#,##0.##");
			int indexTalk = 0;
			for (Talk talk : votes.getTalks()) {
				indexTalk ++;
				result.append("  At position "+indexTalk+" .");
				result.append("  With an average of " + decimalFormat.format(Double.valueOf(talk.getAvg()).doubleValue())+ ".");
				result.append(talk.getTitle() + ", by ");
				int index = 0;
				int nbrSpeakers = talk.getSpeakers().length;
				for (String speaker : talk.getSpeakers()) {
					index++;
					result.append(StringUtils.trim(speaker));
					if (index < nbrSpeakers) {
						result.append(" and ");
					}
					logger.info("Speaker:" + speaker + ":");
				}
				result.append(".");
			}
			}
			else {
				result = new StringBuffer("No results were provided. Sorry !");
			}


		return result.toString();
	}
}

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
 * @author danieldeluca
 *
 */
public class ConferenceVotingRestClient {
	private static final Logger logger = LogManager.getLogger(ConferenceVotingRestClient.class.getName());


	// Voting API Documentation : https://bitbucket.org/jonmort/devoxx-vote-api
	private static final String CONFERENCE_VOTING_HOST = "https://api-voting.devoxx.com";
	private static final String CONFERENCE_PATH_VOTING = "/DV15/top/talks";

	private Client restClient = null;
	private WebTarget webTarget = null;
	
	private String topConfTalkName = null;
	private String topDayTalkName = null;

	public static void main(String[] args) throws Exception {
		ConferenceVotingRestClient client = new ConferenceVotingRestClient();
		client.getTopTalks(5, false);
		client.getTopTalks(5, false);
		client.hasTopTalkChanged(true);
		client.hasTopTalkChanged(false);
		client.hasTopTalkChanged(true);
		client.hasTopTalkChanged(false);		
	}

	public ConferenceVotingRestClient() {
		restClient = ClientBuilder.newClient();
	}
	
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
		Votes votes = getRawVotes(1, today);
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
				for (String speaker : currentTopTalk.getSpeakers()) {
					index++;
					result.append(StringUtils.trim(speaker));
					if (index < nbrSpeakers) {
						result.append(" and ");
					}
					logger.info("Speaker:" + speaker + ":");
				}
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
	
	private Votes getRawVotes(int limit, boolean today){
		Votes votes = null;
		String restPath = CONFERENCE_PATH_VOTING;
		if (today){
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
			String dayOfWeek = dateFormat.format(date);
			webTarget = restClient.target(CONFERENCE_VOTING_HOST).path(restPath).queryParam("limit", limit).queryParam("day", dayOfWeek.toLowerCase());
		}
		else {
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

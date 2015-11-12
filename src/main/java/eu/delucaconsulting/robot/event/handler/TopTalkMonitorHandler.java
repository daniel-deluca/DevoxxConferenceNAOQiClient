/**
 * 
 */
package eu.delucaconsulting.robot.event.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import eu.delucaconsulting.robot.rest.client.ConferenceVotingRestClient;

/**
 * @author danieldeluca
 *
 */
public class TopTalkMonitorHandler {
	private static final Logger logger = LogManager.getLogger(TopTalkMonitorHandler.class.getName());
	ALMemory memory = null;
	ALTextToSpeech tts = null;

	public void run(Session session) throws Exception {

		memory = new ALMemory(session);
		tts = new ALTextToSpeech(session);

		ConferenceVotingRestClient conferenceClient = new ConferenceVotingRestClient();
		String result = null;

		while (true) {
			logger.info("checking TOP talk change");
			result = conferenceClient.hasTopTalkChanged(true);
			if (result != null) {
				tts.say(result);
			}
			result = conferenceClient.hasTopTalkChanged(false);
			if (result != null) {
				tts.say(result);
			}
			Thread.sleep(300000); // 5 min
		}
	}

}

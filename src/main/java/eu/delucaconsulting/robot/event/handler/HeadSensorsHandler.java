/**
 * 
 */
package eu.delucaconsulting.robot.event.handler;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import eu.delucaconsulting.robot.rest.client.ConferenceScheduleRestClient;
import eu.delucaconsulting.robot.rest.client.ConferenceVotingRestClient;

/**
 * @author danieldeluca
 *
 */
public class HeadSensorsHandler {
    ALMemory memory = null;
    ALTextToSpeech tts = null;
    long frontTactilSubscriptionId = 0;
    long middleTactilSubscriptionId = 0;
	private static final String FRONT_EVENT_NAME = "FrontTactilTouched";
	private static final String MIDDLE_EVENT_NAME = "MiddleTactilTouched";

    public void run(Session session) throws Exception {

        memory = new ALMemory(session);
        tts = new ALTextToSpeech(session);

        // Subscribe to FrontTactilTouched event,
        // create an EventCallback expecting a Float.
        frontTactilSubscriptionId = memory.subscribeToEvent(FRONT_EVENT_NAME, new EventCallback<Float>() {
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        // 1 means the sensor has been pressed
                        if (arg0 > 0) {
                            ConferenceScheduleRestClient conferenceClient = new ConferenceScheduleRestClient();
                            tts.say(conferenceClient.getCurrentSessions());
                        }
                    }
                });
        // Subscribe to MiddleTactilTouched event,
        // create an EventCallback expecting a Float.
        middleTactilSubscriptionId = memory.subscribeToEvent(MIDDLE_EVENT_NAME, new EventCallback<Float>() {
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        // 1 means the sensor has been pressed
                        if (arg0 > 0) {
                            ConferenceVotingRestClient conferenceClient = new ConferenceVotingRestClient();
                            tts.say(conferenceClient.getTopTalks(5, true));
                        }
                    }
                });
        // Subscribe to RearTactilTouched event,
        // create an EventCallback expecting a Float.
        memory.subscribeToEvent("RearTactilTouched",
                new EventCallback<Float>() {
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        if (arg0 > 0) {
                            if (frontTactilSubscriptionId > 0) {
                                tts.say("I'll no longer say anything");
                                // Unsubscribing from FrontTactilTouched event
                                memory.unsubscribeToEvent(frontTactilSubscriptionId);
                                frontTactilSubscriptionId = 0;
                            }
                        }
                    }
                });
    }

}

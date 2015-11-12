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
 * Robot Head Sensors event subscription
 * When Front Sensor touched, the robot queries the CFP API to get the current conference sessions and say the details
 * When Middle Sensor touched, the robot queries the Voting Services in order to get the TOP_X_SESSION Sessions
 * When Rear Sensor touched, we unsubscribe to both previous events.
 * @author danieldeluca
 *
 */
public class HeadSensorsHandler {
    ALMemory memory = null;
    ALTextToSpeech tts = null;
    long frontTactilSubscriptionId = 0;
    long middleTactilSubscriptionId = 0;
    private static final int TOP_X_SESSION = 5;
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
                            tts.say(conferenceClient.getTopTalks(TOP_X_SESSION, true));
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
                                // Unsubscribing from the 2 events event
                                memory.unsubscribeToEvent(frontTactilSubscriptionId);
                                memory.unsubscribeToEvent(middleTactilSubscriptionId);
                                frontTactilSubscriptionId = 0;
                                middleTactilSubscriptionId = 0;
                            }
                        }
                    }
                });
    }

}

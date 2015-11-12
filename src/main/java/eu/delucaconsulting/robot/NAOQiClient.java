/**
 * Daniel De Luca
 */
package eu.delucaconsulting.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import eu.delucaconsulting.robot.event.handler.BarcodeReaderHandler;
import eu.delucaconsulting.robot.event.handler.HeadSensorsHandler;
import eu.delucaconsulting.robot.event.handler.TopTalkMonitorHandler;

/**
 * @author danieldeluca
 *
 */
public class NAOQiClient {
	private static final Logger logger = LogManager.getLogger(NAOQiClient.class.getName());
    ALMemory memory = null;
    ALTextToSpeech tts = null;
    long frontTactilSubscriptionId;

    /**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        String robotUrl = "tcp://nao3.local:9559";
        // Create a new application
        Application application = new Application(args, robotUrl);
        // Start your application
        application.start();
        HeadSensorsHandler headSensorsHandler = new HeadSensorsHandler();
        headSensorsHandler.run(application.session());
        BarcodeReaderHandler barcodeReaderHandler = new BarcodeReaderHandler();
        barcodeReaderHandler.run(application.session());
        TopTalkMonitorHandler topTalkMonitorHandler = new TopTalkMonitorHandler();
        topTalkMonitorHandler.run(application.session());
        
        logger.info("Ready");
        application.run();
	}
}

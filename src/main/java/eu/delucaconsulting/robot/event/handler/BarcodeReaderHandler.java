/**
 * 
 */
package eu.delucaconsulting.robot.event.handler;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBarcodeReader;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;


/**
 * @author danieldeluca
 *
 */
public class BarcodeReaderHandler {
	private static final Logger logger = LogManager.getLogger(BarcodeReaderHandler.class.getName());
	ALMemory memory = null;
	ALBarcodeReader barcodeReader = null; 
	ALTextToSpeech tts = null;
	long barCodeEventSubscrptionId = 0;
	private static final String EVENT_NAME = "BarcodeReader/BarcodeDetected";

	public void run(Session session){
		try {
			memory = new ALMemory(session);
			barcodeReader = new ALBarcodeReader(session);
			tts = new ALTextToSpeech(session);
			barcodeReader.setResolution(1);

			logger.info("Starting Subscription to BarcodeReader detection event:"
					+barcodeReader.getActiveCamera()+":"
					+barcodeReader.getCurrentPrecision()+":"
					+barcodeReader.getFrameRate()+":"
					+barcodeReader.getResolution()+":"
					);
			// Subscribe to BarcodeReader event,
			// create an EventCallback expecting a Float.
			barCodeEventSubscrptionId = memory.subscribeToEvent(EVENT_NAME, new EventCallback<ArrayList<ArrayList<String>>>() {
						public void onEvent(ArrayList<ArrayList<String>> data)
								throws InterruptedException, CallError {
							logger.info("Detected:" + data);
							if ((data.size() > 0) && (data.get(0).size() >0)){
								ArrayList<String> result = data.get(0);
								logger.info(result.get(0));	
							}
						}
					});
		} catch (Exception e) {
			logger.fatal(e);
			System.exit(-1);
		}
	}

}

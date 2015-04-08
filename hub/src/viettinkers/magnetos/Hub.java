package viettinkers.magnetos;

import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

/**
 * The Magnetos hub's running logics.
 * 
 * @author vietanh
 * 
 */
public class Hub {

  // XBee configurations.
  private static final String USB_PORT = "COM5";
  private static final String LOG_PROP_PATH = "log4j.properties";

  // Magneto mapping.
  private static final String MAGNETO_FOOD_ADDR = "0x00,0x13,0xa2,0x00,0x40,0xd8,0x5f,0xcf";

  private XBee xbee;

  /**
   * Initializes services and configurations.
   */
  public void init() throws Exception {
    PropertyConfigurator.configure(LOG_PROP_PATH);
    xbee = new XBee();
    xbee.open(USB_PORT, 9600);
  }

  /**
   * Executes all services.
   */
  public void run() throws Exception {
    while (true) {
      XBeeResponse response = xbee.getResponse();
      if (response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE) {
        ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
        handleXBeeResponse(ioSample);
      }
    }
  }

  /**
   * Handles XBee response as soon as a new response is received.
   * 
   * @param iosample
   */
  private void handleXBeeResponse(ZNetRxIoSampleResponse ioSample) {
    if (ioSample.getRemoteAddress64().toString().equals(MAGNETO_FOOD_ADDR)) {
      handleFoodMagneto(ioSample.isD0On());
    }
  }

  /**
   * Handles Food Magneto triggers.
   * 
   * @param isReleased
   *          True if the Magneto is released, False if it's still pressed.
   */
  private void handleFoodMagneto(boolean isReleased) {
    if (!isReleased) {
      AudioCapturer.getInstance().start();
    } else {
      AudioCapturer.getInstance().stop();
      AudioCapturer.getInstance().play();
    }
  }

  /**
   * Runs the program.
   */
  public static void main(String[] args) throws Exception {
    Hub hub = new Hub();
    hub.init();
    hub.run();
  }
}

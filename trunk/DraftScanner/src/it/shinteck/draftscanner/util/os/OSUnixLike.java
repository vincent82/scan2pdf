package it.shinteck.draftscanner.util.os;

import Lib3.DBInterface.hiTechContainer;
import com.asprise.util.jsane.JSane;
import com.asprise.util.jsane.JSaneDevice;
import com.asprise.util.jtwain.Source;
import it.shinteck.draftscanner.util.ScannerFunction;
import java.awt.Image;
import org.apache.log4j.Logger;

/**
 *
 * @author vincenzo
 */
public class OSUnixLike implements ScannerFunction {

    private static Logger logger = Logger.getLogger(OSUnixLike.class);
    private JSaneDevice[] devices = null;
    private Source source = null;

    public boolean selectSource(hiTechContainer hitechCont) throws Exception {

        JSane sane = new JSane("127.0.0.1");

        logger.info(sane.getHost());
        logger.info("Inizializzo dispositivi...");
        this.devices = sane.getAllDevices();
        int deviceNumer = this.devices.length;
        logger.info("Found " + deviceNumer + " devices!");
        return true;
    }

    public Image preview() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getMessage() {
        return UNIX;
    }

    public String getImageTempAbsolutePath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancellaImgTemp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


package it.shinteck.draftscanner.util;

import it.shinteck.draftscanner.util.os.OSUnixLike;
import it.shinteck.draftscanner.util.os.OSWindows;
import org.apache.log4j.Logger;

/**
 *
 * @author vincenzo
 */
public class OSTypeFactory {
    private static Logger logger = Logger.getLogger(OSTypeFactory.class);

    
    /**
     * Controlla il tipo di Sistema Operativo e richiama i metodi opportuni.
     * @return
     */
    public static ScannerFunction getInstance(){
        logger.info("Testing OS..");
        ScannerFunction sistema=null;
        String systemName=System.getProperty("os.name").toLowerCase();

        if(systemName.contains("window")){
            sistema=new OSWindows();
        }else{
            sistema=new OSUnixLike();
        }
        logger.info(sistema.getMessage());
        return sistema;
    }
}

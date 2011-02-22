package it.shinteck.draftscanner.util.os;

import Lib3.DBInterface.XmlPropertiesReader;
import Lib3.DBInterface.hiTechContainer;
import com.asprise.util.jtwain.Source;
import com.asprise.util.jtwain.SourceManager;
import it.shinteck.draftscanner.util.ScannerFunction;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author vincenzo
 */
public class OSWindows implements ScannerFunction {

    private static Logger logger = Logger.getLogger(OSWindows.class);
    private Source source = null;
    private Image image;
    private File temp_image;
    private hiTechContainer hitechCont;
    private XmlPropertiesReader xmlProps;

    public boolean selectSource(hiTechContainer hitechCont) throws Exception {
        if(hitechCont!=null){
            xmlProps=hitechCont.getXmlProps();
        }
        //recupera tutte le sorgenti rilevate
        Source[] sorgenti = SourceManager.instance().getAllSources();
        int sourceNumber = sorgenti.length;
        logger.info(sourceNumber + " sorgenti rilevate:");
        int i = sourceNumber;
        while (i > 0) {
            i--;
            logger.info(sorgenti[i].toString(true));
        }
        /* se rileva una sola sorgente, questa viene identificata come di default
         * e la seleziona automaticamente senza mostrare il pop-up di selezione della sorgente.
         */
        if (sourceNumber == 1) {
            logger.info("Seleziono la sorgente di default");
            this.source = SourceManager.instance().getDefaultSource();
        } else {
            //mostra il popup di selezione della sorgente
            //this.source = SourceManager.instance().selectSourceUI();
        }

        logger.info("Controllo se la sorgente è attiva");
        //this.source = SourceManager.instance().selectSource(this.source);
        this.source=SourceManager.instance().selectSourceByName(this.getProperties("scanner"));
        logger.info("Selezionata: "+this.source);
        if (this.source == null) {
            logger.info("Sorgente non attiva! Uscita...");
            return false;
        } else {
            logger.info("Sorgente attiva!");
            return true;
        }
    }
    /**
     * Recupera il valore corrispondente alla chiave specificata nel file di properties
     * Il file di properties può essere standard java o personalizzato secondo il formato Hitech
     * @param key, la chiave richiesta
     * @return il valore per quella chiave
     */
    private String getProperties(String key) throws  Exception{
        if (xmlProps == null || hitechCont == null) {
            Properties props = new Properties();
            props.load(new FileInputStream("Properties.properties"));
            return props.getProperty(key);
        } else {
            return xmlProps.leggiStr(key);
        }
    }

    public Image preview() throws Exception {
        try {
            this.source.setUIEnabled(false);
            this.source.open();
            //seleziono una determinata area dello scanner
            this.source.setRegion(0, 0, this.source.getPhyscialWidth(), this.source.getPhysicalHeight()/2);
            this.source.set
            image = this.source.acquireImage();
            //Creo il file immagine
            temp_image=File.createTempFile("ricettaNiguarda", ".jpg");
            this.source.saveLastAcquiredImageIntoFile(temp_image);
            this.source.close();
            logger.info("Immagine salvata in "+temp_image.getAbsolutePath());
            logger.info("Closing SourceManager..");
            SourceManager.closeSourceManager();
            return image;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.info("Closing SourceManager with error..");
            SourceManager.closeSourceManager();
            throw new Exception(ex);
        }
    }

    public String getMessage() {
        return WINDOWS;
    }

    public String getImageTempAbsolutePath(){
        return this.temp_image.getAbsolutePath();
    }

    public void cancellaImgTemp() {
        this.temp_image.deleteOnExit();
    }
}

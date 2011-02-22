package it.shinteck.draftscanner;

import it.shinteck.draftscanner.util.OSTypeFactory;
import it.shinteck.draftscanner.util.PDFUtil;
import it.shinteck.draftscanner.util.ScannerFunction;
import it.shinteck.draftscanner.view.ImageDisplayer;
import java.awt.Image;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author vincenzo
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class);
    private static ScannerFunction scanner;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            logger.info("Testing OS");
            scanner = OSTypeFactory.getInstance();
            boolean okScanner = scanner.selectSource(null);
            if (okScanner) {
                logger.info("Waiting for preview..");
                Image image = scanner.preview();
                logger.info("Preview setted!");
                HashMap<String, String> info = new HashMap<String, String>();
                info.put("Subject", "Ricetta Niguarda");
                info.put("Title", "titolo_prova");
                info.put("numero pratica", "1000");
                logger.info("metadati: " + info);
                if (image == null) {
                    logger.info("image not scanned");
                }else{
                    logger.info("Creo il pdf con l'immagine "+scanner.getImageTempAbsolutePath());
                    PDFUtil pdf=new PDFUtil();
                    byte[] result=pdf.creaPDF(scanner.getImageTempAbsolutePath(), info);
                    logger.info("Creato l'array");
                    logger.info("Inizializzo il pannellino");
                    //pdf.cancellaTemp();
                    ImageDisplayer panel=new ImageDisplayer(image);
                    scanner.cancellaImgTemp();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*public static void main(String[] args) {
    try {
    logger.info("Testing OS");

    scanner = OSTypeFactory.getInstance();
    boolean okScanner = scanner.selectSource();
    Icon icon = new ImageIcon("./Resources/icone/tavolozza.gif");

    JOptionPane.showMessageDialog(new JPanel(), "Welcome to DraftScanner", "Welcome", JOptionPane.INFORMATION_MESSAGE, icon);

    if (okScanner) {
    logger.info("Waiting for preview..");
    Image image = scanner.preview();
    logger.info("Preview setted!");
    HashMap<String, String> info = new HashMap<String, String>();
    info.put("Subject", "Ricetta Niguarda");
    info.put("Title", "titolo_prova");
    info.put("numero pratica", "1000");
    logger.info("info: "+info);
    if (image == null) {
    logger.info("image not scanned");
    } else {

    DraftScannerView view = new DraftScannerView(null, image, info, scanner.getImageTempAbsolutePath());
    logger.info("creazione del pannello modale...");
    view.setVisible(true);
    //view.hiTechStartModal();
    logger.info("Pannello creato!");
    logger.info("byte di ritorno: " + view.getByte());
    logger.info("Cancello il file immagine temporaneo "+scanner.getImageTempAbsolutePath());
    scanner.cancellaImgTemp();
    }
    }
    } catch (Exception e) {
    logger.error(e.getMessage(), e);
    JOptionPane.showMessageDialog(new JPanel(), e, "Exception", JOptionPane.ERROR_MESSAGE);
    }
    }*/
}


package it.shinteck.draftscanner.util;

import Lib3.DBInterface.hiTechContainer;
import java.awt.Image;

/**
 *
 * @author vincenzo
 */
public interface ScannerFunction {
    String ERR_CLOSING_SOURCE_MANAGER="Errore durante la chiusura del source manager!";
    String WINDOWS="Windows OS";
    String UNIX="Unix like OS";
    String CONVERTING="Converting image into PDF";
    String CONVERTION_SUCCESS="File immagine convertito con successo";


    /**
     * Seleziona la sorgente 
     * @return 'true' se la sorgente è raggiungibile.
     */
    public boolean selectSource(hiTechContainer hitechCont) throws Exception;

    /**
     * Mostra il preview dell'immagine scansionata dalla sorgente.
     * Contestualmente salva l'immagine in una cartella temporanea.
     */
    public Image preview() throws Exception;

    /**
     * Ritorna un messaggio di notifica riguardo il Sistema Operativo rilevato.
     * @return
     */
    public String getMessage();

    /**
     * Ritorna una stringa indicante il percorso assoluto del file immagine prodotto
     * @return
     */
    public String getImageTempAbsolutePath();

    /**
     * Cancella il file immagine temporaneo
     * @return
     */
    public void cancellaImgTemp();
}

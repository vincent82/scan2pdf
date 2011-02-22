package it.shinteck.draftscanner.util;

import Lib3.DBInterface.*;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import it.shinteck.draftscanner.util.exception.OperationNotCompletedException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author vincenzo
 */
public class PDFUtil {

    private Image image;
    private final String CONVERTION_SUCCESS = "File immagine convertito con successo";
    private static Logger logger = Logger.getLogger(PDFUtil.class);
    private hiTechContainer hitechCont;
    private XmlPropertiesReader xmlProps;
    private boolean hitechProps;


    public PDFUtil(){
    }
    
    public PDFUtil(hiTechContainer hitechCont) {
        this.hitechCont = hitechCont;
        this.xmlProps = hitechCont.getXmlProps();
    }

    /**
     * Converte l'immagine in un formato utile a iText per la conversione in PDF
     * @param nameImage
     */
    private void setImage(String nameImage) throws BadElementException, IOException{
        File file = new File(nameImage);
        byte[] yte = staticMethods.getBytesFromFile(file);
        this.image = Image.getInstance(yte);
        //this.image=Image.getInstance(nameImage);
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
            this.hitechProps=false;
            return props.getProperty(key);
        } else {
            this.hitechProps=true;
            return xmlProps.leggiStr(key);
        }
    }

    public byte[] creaPDF(String imageFile, HashMap<String, String> metadata) throws Exception {
        String ricetta = this.getProperties("Resource_path") + this.getProperties("RicettaPDF");
        logger.info("Sto creando il PDF..");
        Document document = new Document();
        try {
            File dir = new File(this.getProperties("Resource_path"));
            //se non esiste, creo la cartella di destinazione
            if (!dir.exists()) {
                dir.mkdir();
            }
            //costruisco il path relativo su dove verrà memorizzato il temporary file pdf
            String temp = this.getProperties("Resource_path") + this.getProperties("PDF_temp");
            // creo il documento pdf in cui convertire l'immagine
            File tempPDF = new File(temp);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempPDF));
            document.open();

            //Aggiungo un nuovo paragrafo nel documento PDF
            document.add(new Paragraph("Ricetta di test"));
            //preparo l'immagine per inserirla nel file pdf
            setImage(imageFile);
            //effettuo lo scaling dell'immagine
            int absoluteHeight;
            int absoluteWidth;
            int dpiX;
            int dpiY;
            if (this.hitechProps == true) {
                absoluteHeight=xmlProps.leggiInt("absoluteHeight");
                absoluteWidth=xmlProps.leggiInt("absoluteWidth");
                dpiX=xmlProps.leggiInt("dpiX");
                dpiY=xmlProps.leggiInt("dpiY");
            } else {
                absoluteHeight=new Integer(this.getProperties("absoluteHeight"));
                absoluteWidth=new Integer(this.getProperties("absoluteWidth"));
                dpiX=new Integer(this.getProperties("dpiX"));
                dpiY=new Integer(this.getProperties("dpiY"));
            }
            image.setDpi(dpiX, dpiY);
            image.scaleAbsoluteHeight(absoluteHeight);
            image.scaleAbsoluteWidth(absoluteWidth);
            logger.debug("scaled height: " + image.getScaledHeight());
            logger.debug("scaled width: " + image.getScaledWidth());
            logger.debug("dpi: "+image.getDpiX()+", "+image.getDpiY());

            //aggiungo l'immagine al documento PDF
            boolean result = document.add(image);
            //chiudo il documento PDF
            document.close();
            writer.close();
            
            if (result) {
                logger.info(CONVERTION_SUCCESS);
                File nuovaRicetta;
                if (metadata == null) {
                    nuovaRicetta = new File(ricetta);
                    //rinomino il file
                    tempPDF.renameTo(nuovaRicetta);
                    logger.info("Creata la ricetta " + nuovaRicetta.getAbsolutePath());
                } else {
                    //se presenti, aggiungo i metadata
                    logger.info("Aggiungo i metadati");
                    nuovaRicetta=this.setMetadata(metadata);
                }
                //Cancello il file temporaneo
                tempPDF.delete();
                logger.debug("Preparo l'array di byte...");
                byte[] streamPDF= this.toArrayByte(nuovaRicetta);
                //logger.info("Metadati dello stream: "+this.getMetadati(this.toArrayByte(nuovaRicetta)));
                return streamPDF;
            } else {
                throw new OperationNotCompletedException();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (document.isOpen()) {
                document.close();
            }
            return null;
        }
    }

    /**
     * Converte da byte[] in un qualunque file
     * @param docPDF
     * @return
     */
    /*private void byteArray2File(byte[] arrayByte) {
    try {
    String dest = this.getProperties("Resource_path") + this.getProperties("Copia_temp_path");
    FileOutputStream fos = new FileOutputStream("./copia.pdf");
    fos.write(arrayByte);
    fos.close();
    } catch (Exception e) {
    logger.error("Errore: " + e);
    }
    }*/
    
    /**
     * Converte un qualunque file in un byte[]
     * @param file
     * @return
     */
    private byte[] toArrayByte(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] temp = new byte[fis.available()];  // numero di byte di
        // cui è composto il file
        fis.read(temp);
        logger.debug("Array completato!");
        return temp;
    }

    /**
     * Aggiunge i metadati a un documento PDF già esistente.
     * @param docPDF, il documento che deve essere arricchito con i metadata
     * @param metadata, i metadata da aggiungere
     * @return un array di byte rappresentante il documento PDF arricchito
     */
    /*public byte[] setMetadata(byte[] docPDF, HashMap<String, String> metadata) throws Exception {
        String tempoPDF = this.getProperties("Resource_path") + this.getProperties("PDF_temp");
        String ricetta = this.getProperties("Resource_path") + this.getProperties("RicettaPDF");
        File filePDF = new File(tempoPDF);
        PdfReader reader = new PdfReader(docPDF);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePDF));
        //aggiungo i metadata
        stamper.setMoreInfo(metadata);
        stamper.close();
        reader.close();
        //rinomino il file
        File nuovo = new File(ricetta);
        filePDF.renameTo(nuovo);
        logger.info("Creata la ricetta con metadati in: "+nuovo.getAbsolutePath());
        //preparo l'array di byte
        byte[] result = this.toArrayByte(filePDF);
        return result;
    }*/

    /**
     * Aggiunge i metadati a un documento PDF già esistente.
     * Il file è identificato tramite la property 'PDF_temp'
     * @param metadata, i metadata da aggiungere
     * @return un array di byte rappresentante il documento PDF arricchito
     */
    private File setMetadata(HashMap<String, String> metadata) throws Exception {
        String tempPDF = this.getProperties("Resource_path") + this.getProperties("PDF_temp");
        File temp=new File(tempPDF);
        String temporary = this.getProperties("Resource_path") + "temporary.pdf";
        String ricetta = this.getProperties("Resource_path") + this.getProperties("RicettaPDF");
        File filePDF = new File(temporary);
        //apro il file pdf esistente
        PdfReader reader = new PdfReader(this.toArrayByte(temp));
        //lo stamper crea un nuovo pdf con i metadati aggiunti
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePDF));
        //aggiungo i metadati
        logger.debug("Aggiungo i metadati...");
        stamper.setMoreInfo(metadata);
        stamper.close();
        reader.close();
        //rinomino il file
        File nuovo = new File(ricetta);
        filePDF.renameTo(nuovo);
        //cancello il file temporaneo
        filePDF.delete();
        logger.debug("Metadati aggiunti!");
        logger.info("Creata la ricetta con metadati in: "+nuovo.getAbsolutePath());
        return nuovo;
    }

    /**
     * Legge i metadati XMP di un file PDF
     * @param pdfIn
     * @return
     * @throws Exception
     */
    public HashMap<String, String> getMetadati(byte[] pdfIn) throws Exception{
        PdfReader reader=new PdfReader(pdfIn);
        logger.info("leggo i metadati dallo stream ");
        HashMap<String, String> result=reader.getInfo();
        reader.close();
        return result;
    }
}

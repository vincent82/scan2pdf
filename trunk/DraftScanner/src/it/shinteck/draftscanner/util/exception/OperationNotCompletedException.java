/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.shinteck.draftscanner.util.exception;

/**
 *
 * @author vincenzo
 */
public class OperationNotCompletedException extends Exception {

    public OperationNotCompletedException() {
        super("Operazione non completata!");
    }
}

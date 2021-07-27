/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Ong Bik Jeun
 */
public class CreateDocumentException extends Exception {

    /**
     * Creates a new instance of <code>CreateDocumentException</code> without
     * detail message.
     */
    public CreateDocumentException() {
    }

    /**
     * Constructs an instance of <code>CreateDocumentException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateDocumentException(String msg) {
        super(msg);
    }
}

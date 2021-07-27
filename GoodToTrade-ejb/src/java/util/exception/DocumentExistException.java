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
public class DocumentExistException extends Exception {

    /**
     * Creates a new instance of <code>DocumentExistException</code> without
     * detail message.
     */
    public DocumentExistException() {
    }

    /**
     * Constructs an instance of <code>DocumentExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DocumentExistException(String msg) {
        super(msg);
    }
}

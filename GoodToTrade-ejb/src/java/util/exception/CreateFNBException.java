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
public class CreateFNBException extends Exception {

    /**
     * Creates a new instance of <code>CreateFNBException</code> without detail
     * message.
     */
    public CreateFNBException() {
    }

    /**
     * Constructs an instance of <code>CreateFNBException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateFNBException(String msg) {
        super(msg);
    }
}

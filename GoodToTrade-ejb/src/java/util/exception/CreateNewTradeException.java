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
public class CreateNewTradeException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewTradeException</code> without
     * detail message.
     */
    public CreateNewTradeException() {
    }

    /**
     * Constructs an instance of <code>CreateNewTradeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewTradeException(String msg) {
        super(msg);
    }
}

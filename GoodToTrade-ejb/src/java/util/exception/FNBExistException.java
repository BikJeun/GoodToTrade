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
public class FNBExistException extends Exception {

    /**
     * Creates a new instance of <code>FNBExistException</code> without detail
     * message.
     */
    public FNBExistException() {
    }

    /**
     * Constructs an instance of <code>FNBExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FNBExistException(String msg) {
        super(msg);
    }
}

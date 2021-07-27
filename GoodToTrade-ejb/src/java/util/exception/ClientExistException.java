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
public class ClientExistException extends Exception {

    /**
     * Creates a new instance of <code>ClientExistException</code> without
     * detail message.
     */
    public ClientExistException() {
    }

    /**
     * Constructs an instance of <code>ClientExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ClientExistException(String msg) {
        super(msg);
    }
}

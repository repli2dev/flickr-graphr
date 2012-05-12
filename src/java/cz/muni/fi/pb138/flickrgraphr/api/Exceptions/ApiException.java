/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.api.Exceptions;

/**
 *
 * @author mantaexx
 */
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}

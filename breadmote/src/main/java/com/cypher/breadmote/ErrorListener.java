package com.cypher.breadmote;

import java.util.List;

/**
 * Used to actively listen to errors while connected to a device
 *
 * @see Connection#addListener(ErrorListener)
 * @see Connection#removeListener(ErrorListener)
 */
public interface ErrorListener {
    /**
     * Called when a new error is generated, either by the BreadMote SDK or the connected device
     * @param error The newly generated error. Note: The error is automatically appeneded to the list of errors
     */
    void onError(Error error);

    /**
     * Called when an error is removed
     *
     * @param index The index of the error removed. Note: The error no longer exists in the list of errors
     * @see Connection#removeError(int)
     */
    void onErrorRemoved(int index);

    /**
     * Called when the error log has been cleared or when the listener is first added
     *
     * @param errors The current list of errors. This collection cannot be modified
     * @see Connection#clearErrors()
     */
    void setErrors(List<Error> errors);
}

package it.unimib.unimibmodules.exception;
/**
 * Exception class for not found entities.
 * @author Luca Milazzo
 * @version 0.4.1
 */
public class NotFoundException extends Exception{
	/**
	 * Constructs an FormatException with the specified message and exception data.
	 * @param	message	the exception message
	 * @param err the root exception object
	 */
	public NotFoundException(String message, Throwable err) {
		super(message, err);
	}
	
	public NotFoundException(String message) {

		super(message);
	}
}

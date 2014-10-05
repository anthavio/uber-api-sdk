package net.anthavio.uber;

import net.anthavio.uber.response.UberError;

/**
 * 
 * @author martin.vanek
 *
 */
public class UberException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UberException(String message, Throwable cause) {
		super(message, cause);
	}

	public UberException(String message) {
		super(message);
	}

	public UberException(Throwable cause) {
		super(cause);
	}

	public UberException(int httpStatus, UberError error) {
		super("HTTP: " + httpStatus + " message: " + error.toString());
	}

}

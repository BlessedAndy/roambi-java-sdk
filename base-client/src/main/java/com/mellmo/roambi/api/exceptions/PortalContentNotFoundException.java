/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.exceptions;

public class PortalContentNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PortalContentNotFoundException(String msg) {
		super(msg);
	}
}

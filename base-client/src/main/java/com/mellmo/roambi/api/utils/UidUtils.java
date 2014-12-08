/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.utils;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author charles
 *
 */
public abstract class UidUtils {
	
	private static final String UID_PATTERN = "^[0-9a-fA-F]{24}$";

	public static boolean isUuid(final String id) {
		try {
			return UUID.fromString(id) != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	public static boolean isUid(final String id) {
		return id != null && id.matches(UID_PATTERN);
	}
	
	public static boolean isPath(final String id) {
		return StringUtils.contains(id, '/');
	}
	
	public static boolean isEmail(final String id) {
		return StringUtils.contains(id, '@');
	}

}

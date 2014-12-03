/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author charles
 *
 */
public abstract class UidValidator {
	
	private static final String EMAIL_PATTERN = "^[a-z0-9_\\+-]+(\\.[a-z0-9_\\+-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,4})$";
	private static final String UID_PATTERN = "^[0-9a-fA-F]{24}$";
	final private String id;
	private boolean isBlank = false;
	private boolean isPath = false;
	private boolean isUid = false;
	private boolean isUuid = false;
	private boolean isEmail = false;
	
	public UidValidator(final String id) {
		this.id = id;
		this.isBlank = isBlank(id);
		if (!this.isBlank) {
			this.isPath = isPath(id);
			if (!this.isPath) {
				this.isUid = isUid(id);
				if (!this.isUid) {
					this.isUuid = isUuid(id);
					if (!this.isUuid) {
						this.isEmail = isEmail(id);
					}
				}
			}
		}
	}
	
	public static boolean isBlank(final String id) {
		return StringUtils.isBlank(id);
	}
	
	public static boolean isEmail(final String id) {
		return id != null && id.toLowerCase().matches(EMAIL_PATTERN);
	}
	
	public static boolean isPath(final String id) {
		return StringUtils.indexOf(id, '/') >= 0;
	}
	
	public static boolean isUid(final String id) {
		return id != null && id.matches(UID_PATTERN);
	}
	
	public static boolean isUuid(final String id) {
		try {
			return UUID.fromString(id) != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public String getId() {
		return this.id;
	}
	
	public boolean isBlank() {
		return this.isBlank;
	}
	
	public boolean isEmail() {
		return this.isEmail;
	}
	
	public boolean isPath() {
		return this.isPath;
	}
	
	public boolean isUid() {
		return this.isUid;
	}
	
	public boolean isUuid() {
		return this.isUuid;
	}
	
	public boolean isValid() {
		return !isBlank() && !isPath() && isValidUid();
	}
	
	public abstract boolean isValidUid();
}

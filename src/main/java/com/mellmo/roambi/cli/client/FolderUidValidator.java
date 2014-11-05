/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import org.apache.commons.lang3.StringUtils;

import com.mellmo.roambi.api.model.User;

/**
 * 
 * @author charles
 *
 */
public abstract class FolderUidValidator extends UidValidator {
	
	public FolderUidValidator(final String id) {
		super(id);
	}
	
	public static boolean isAccountFolder(final String id) {
		return isUid(id);
	}
	
	public static boolean isPersonalFolder(final String id) {
		return isUuid(id) || isEmail(id);
	}
	
	public static boolean isPersonalFolderOwner(final String id, final User user) {
		return isPersonalFolder(id) && (StringUtils.equalsIgnoreCase(user.getPrimaryEmail(), id) || StringUtils.equals(user.getUid(), id));
	}
	
	public static FolderUidValidator expectAccountFolder(final String id) {
		return new FolderUidValidator(id) {
			@Override public boolean isValidUid() {
				return isAccountFolder(); 
			}
		};
	}
	
	public static FolderUidValidator expectAnyFolder(final String id) {
		return new FolderUidValidator(id) {
			@Override public boolean isValidUid() {
				return isPersonalFolder() || isAccountFolder(); 
			}
		};
	}
	
	public static FolderUidValidator expectPersonalFolder(final String id) {
		return new FolderUidValidator(id) {
			@Override public boolean isValidUid() {
				return isPersonalFolder(); 
			}
		};
	}
	
	public boolean isAccountFolder() {
		return isUid();
	}
	
	public boolean isPersonalFolder() {
		return isUuid() || isEmail();
	}
	
	public boolean isPersonalFolderOwner(final User user) {
		return isPersonalFolderOwner(getId(), user);
	}
}

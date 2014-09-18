/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.http;

import java.io.File;

public class FilePartDescriptor {
	private File file;
	private String fileName;
	
	public FilePartDescriptor() {
	}
	
	public FilePartDescriptor(File file) {
		this(file, (file == null ? null : file.getName()));
	}
	
	public FilePartDescriptor(File file, String fileName) {
		this.file = file;
		this.fileName = fileName;
	}
	
	public File getFile() { return this.file; }
	public void setFile(File f) { this.file = f; }
	
	public String getFileName() { return this.fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

}

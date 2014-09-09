/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

public class ContentResult {
    private ContentItem parentFolder;
    private ContentItem content;

    public ContentItem getParentFolder() {
        return parentFolder;
    }
    
    public ContentItem getContent() {
        return content;
    }
    
    public void setContent(ContentItem content)
    {
        this.content = content;
    }
    
    public void setParentFolder(ContentItem parentFolder)
    {
        this.parentFolder = parentFolder;
    }

}

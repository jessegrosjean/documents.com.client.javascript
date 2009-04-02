package com.hogbaysoftware.documents.client.model;

import java.util.HashMap;

public class Document {
	private static HashMap<String, Document> idsToDocuments = new HashMap<String, Document>();

	private String id;
	private int version = -1;
	private String name;
	private String content = "";
	private boolean hasEdits;

	public static Document getDocumentForID(String id) {
		Document document = idsToDocuments.get(id);
		if (document == null) {
			document = new Document();
			document.setID(id);
			idsToDocuments.put(id, document);
		}
		return document;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		if (this.version != version) {
			this.version = version;
			this.content = null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		String displayName = name;

		if (name == null || name.length() == 0) {
			displayName = "Untitled";
		}
				
		return displayName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean existsOnServer() {
		return id != null;
	}
	
	public boolean hasEdits() {
		return hasEdits;
	}
	
	public void setHasEdits(boolean hasEdits) {
		this.hasEdits = hasEdits;
	}

}

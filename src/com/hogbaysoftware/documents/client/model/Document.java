package com.hogbaysoftware.documents.client.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Document {
	private static HashMap<String, Document> idsToDocuments = new HashMap<String, Document>();

	private String id;
	private int version = -1;
	private String name;
	private String content;
	private ArrayList<DocumentListener> documentListeners;

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
			changed();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		changed();
	}

	public String getDisplayName() {
		if (name == null || name.length() == 0) {
			return "Untitled";
		}
		return name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		changed();
	}

	public boolean existsOnServer() {
		return id != null;
	}

	private void changed() {
		if (documentListeners != null) {
			for (DocumentListener each : documentListeners) {
				each.onChanged(this);
			}
		}
	}
	
	public void addDocumentListener(DocumentListener listener) {
		if (documentListeners == null) {
			documentListeners = new ArrayList<DocumentListener>();
		}
		documentListeners.add(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		if (documentListeners != null) {
			documentListeners.remove(listener);
		}
	}
}

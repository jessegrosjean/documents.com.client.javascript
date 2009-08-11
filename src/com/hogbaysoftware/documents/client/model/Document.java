package com.hogbaysoftware.documents.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.hogbaysoftware.documents.client.Documents;

public class Document {
	private static HashMap<String, Document> idsToDocuments = new HashMap<String, Document>();

	private String id;
	private int version = -1;
	private String name;
	private String content = "";
	private String shadowContent = "";
	private boolean hasEdits;
	private HashMap<String, JSONObject> revisions;

	public static ArrayList<Document> getDocuments() {
		ArrayList<Document> documents = new ArrayList<Document>();
		for (Map.Entry<String, Document> entry : idsToDocuments.entrySet()) {
			documents.add(entry.getValue());
		}
		return documents;
	}
	
	public static Document getDocumentForID(String id, boolean shouldCreate) {
		Document document = idsToDocuments.get(id);
		if (document == null && shouldCreate) {
			document = new Document();
			document.setID(id);
			idsToDocuments.put(id, document);
		}
		return document;
	}
	
	public static Request refreshDocumentsFromServer(final RequestCallback callback) {
		Documents.beginProgress("Loading documents...");
		
		idsToDocuments.clear();

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents");
		builder.setHeader("Content-Type", "application/json");
		
		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load documents\n\n" + e);
					callback.onError(request, e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						try {
							JSONArray jsonDocuments = JSONParser.parse(response.getText()).isArray();
							int size = jsonDocuments.size();

							if (size > 0) {
								for (int i = 0; i < size; i++) {
									JSONObject jsonDocument = jsonDocuments.get(i).isObject();
									Document document = Document.getDocumentForID(jsonDocument.get("id").isString().stringValue(), true);
									document.setVersion((int)jsonDocument.get("version").isNumber().doubleValue());
									document.setName(jsonDocument.get("name").isString().stringValue());
								}
							}
							Documents.endProgressWithAlert(null);
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse documents\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't load documents (" + response.getStatusText() + ")");
					}
					callback.onResponseReceived(request, response);
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load documents\n\n" + e);
		}

		return null;
	}
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		if (this.id != null) {
			idsToDocuments.remove(this.id);
		}
		this.id = id;
		if (this.id != null) {
			idsToDocuments.put(this.id, this);
		}
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		if (this.version != version) {
			this.version = version;
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

		if (name == null || name.length() == 0 || name.equals("Untitled")) {
			if (content != null) {
				int index = Math.min(content.indexOf("\n"), 32);
				if (index > 0) {
					displayName = content.substring(0, index);
				} else if (index != 0){
					displayName = content.substring(0, Math.min(content.length(), 32));
				} else {
					displayName = "";
				}
				
				if (displayName.length() == 0) {
					displayName = "Untitled";
				}
			}
		}
				
		return displayName;
	}

	public String getContent() {
		return content;
	}

	public boolean setContent(String content) {
		if (this.content != null && content != null && this.content.equals(content)) return false;
		this.content = content;
		return true;
	}

	public String getShadowContent() {
		return shadowContent;
	}

	public void setShadowContent(String shadowContent) {
		this.shadowContent = shadowContent;
	}

	public boolean existsOnServer() {
		return version != -1;
	}
	
	public boolean hasEdits() {
		return hasEdits;
	}
	
	public void setHasEdits(boolean hasEdits) {
		this.hasEdits = hasEdits;
	}
	
	public JSONObject getRevision(String key) {
		if (revisions != null) {
			return revisions.get(key);
		}
		return null;
	}

	public void putRevision(String key, JSONObject revision) {
		if (revisions == null) revisions = new HashMap<String, JSONObject>();
		revisions.put(key, revision);
	}
}

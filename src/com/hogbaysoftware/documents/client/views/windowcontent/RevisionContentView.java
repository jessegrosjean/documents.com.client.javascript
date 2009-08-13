package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionContentView extends ContentView {
	private VerticalPanel revisionAttributes = new VerticalPanel();
	
	private String revision;
	
	public RevisionContentView() {
		revisionAttributes.addStyleName("revision");
		initWidget(revisionAttributes);
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions");
	}
	
	public void refreshFromJSONRevision(JSONObject jsonRevision) {
		revisionAttributes.clear();

		revisionAttributes.add(new HTML("<p><strong>" + jsonRevision.get("name").isString().stringValue() + "</strong></p>"));
		
		TextArea contentTextArea = new TextArea();
		JSONValue contentValue = jsonRevision.get("content");
		if (contentValue.isNull() != null) {
			contentTextArea.setText("");
		} else {
			contentTextArea.setText(contentValue.isString().stringValue());
		}
		contentTextArea.setReadOnly(true);
		contentTextArea.addStyleName("content");
		
		revisionAttributes.add(contentTextArea);
		revisionAttributes.setCellWidth(contentTextArea, "100%");
		revisionAttributes.setCellHeight(contentTextArea, "100%");
	}
	
	public Request refreshFromServer() {
		revisionAttributes.clear();
		
		final Document document = Documents.getSharedInstance().getDocument();
		if (!document.existsOnServer()) {
			return null;
		}

		JSONObject jsonRevision = document.getRevision(revision);
		if (jsonRevision != null) {
			refreshFromJSONRevision(jsonRevision);
			return null;
		}
		
		Documents.beginProgress("Loading revision...");
		
		revisionAttributes.add(new Label("Loading..."));

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/" + document.getID() + "/revisions/" + revision);

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load revision\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						try {
							JSONObject jsonRevision = JSONParser.parse(response.getText()).isObject();
							document.putRevision(revision, jsonRevision);
							refreshFromJSONRevision(jsonRevision);
							Documents.endProgressWithAlert(null);
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse revision\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't load revision (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load revision\n\n" + e);
		}
		return null;
	}	
}

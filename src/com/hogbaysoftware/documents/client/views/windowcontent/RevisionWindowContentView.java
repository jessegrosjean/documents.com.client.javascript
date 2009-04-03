package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.TextArea;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionWindowContentView extends WindowContentView {
	//private FlowPanel revisionViewPanel = new FlowPanel();
	private TextArea textArea = new TextArea();
	private String revision;
	
	public RevisionWindowContentView() {
		initWidget(textArea);
		textArea.setReadOnly(true);
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getWindowView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions", revision, document.getID() + "/revisions/" + revision);
		//Documents.getSharedInstance().setWindowTitle(Documents.getSharedInstance().getDocument().getDisplayName() + " > Revisions > " + revision, null);
	}
	
	public void refreshFromJSONRevision(JSONObject jsonRevision) {
		//revisionViewPanel.clear();
		//revisionViewPanel.add(new HTML("<pre>" + jsonRevision.get("content").isString().stringValue() + "</pre>"));
		textArea.setText(jsonRevision.get("content").isString().stringValue());
//		document.setVersion((int) jsonDocument.get("version").isNumber().doubleValue());
	//	document.setName(jsonDocument.get("name").isString().stringValue());
//		document.setContent(jsonDocument.get("content").isString().stringValue());

	}
	
	public Request refreshFromServer() {
		textArea.setText("");
		
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

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/" + document.getID() + "/versions/" + revision);

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

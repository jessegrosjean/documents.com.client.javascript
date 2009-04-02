package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class OpenWindowContentView extends WindowContentView {
	private VerticalPanel documentsViewPanel = new VerticalPanel();
	private VerticalPanel documentsList = new VerticalPanel();

	public OpenWindowContentView() {
		documentsViewPanel.add(documentsList);
		initWidget(documentsViewPanel);
	}

	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().setWindowTitle("Open...", null);
	}

	public Request refreshFromServer() {
		Documents.beginProgress("Loading documents...");
		
		documentsList.clear();

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents");
		builder.setHeader("Content-Type", "application/json");
		
		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load documents\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						try {
							JSONArray jsonDocuments = JSONParser.parse(response.getText()).isArray();
							int size = jsonDocuments.size();

							if (size > 0) {
								for (int i = 0; i < size; i++) {
									JSONObject jsonDocument = jsonDocuments.get(i).isObject();
									Document document = Document.getDocumentForID(jsonDocument.get("id").isString().stringValue());
									document.setVersion((int)jsonDocument.get("version").isNumber().doubleValue());
									document.setName(jsonDocument.get("name").isString().stringValue());
									documentsList.add(new Hyperlink(document.getName(), document.getID()));
								}
							} else {
								documentsList.add(new Label("Your account has no documents."));
//								documentsList.add(new Hyperlink("New Document", "new"));
							}
							Documents.endProgressWithAlert(null);
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse documents\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't load documents (" + response.getStatusText() + ")");
					}
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load documents\n\n" + e);
		}

		return null;
	}
}

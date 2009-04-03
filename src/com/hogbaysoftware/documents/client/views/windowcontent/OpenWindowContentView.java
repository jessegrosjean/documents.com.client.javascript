package com.hogbaysoftware.documents.client.views.windowcontent;

import java.util.ArrayList;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
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
		//Documents.getSharedInstance().setWindowTitle("Open...", null);
		Documents.getSharedInstance().getWindowView().setWindowTitlePath("Open...", "open");
	}

	public Request refreshFromServer() {
		documentsList.clear();

		Document.refreshDocumentsFromServer(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}
			
			public void onResponseReceived(Request request, Response response) {
				ArrayList<Document> documents = Document.getDocuments();
				if (documents.size() == 0) {
					documentsList.add(new Label("Your account has no documents."));
				} else {
					for (Document each : documents) {
						documentsList.add(new Hyperlink(each.getName(), each.getID()));
					}
				}
			}
		});
		
		return null;
/*		Documents.beginProgress("Loading documents...");
		
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
									Document document = Document.getDocumentForID(jsonDocument.get("id").isString().stringValue(), true);
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

		return null;*/
	}
}

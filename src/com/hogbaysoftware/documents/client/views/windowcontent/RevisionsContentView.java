package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionsContentView extends ContentView {
	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel revisionsList = new VerticalPanel();	
	
	public RevisionsContentView() {
		initWidget(scrollPanel);
		scrollPanel.add(revisionsList);
	}
		
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions");
	}

	public Request refreshFromServer() {
		final Document document = Documents.getSharedInstance().getDocument();
		if (!document.existsOnServer()) {
			return null;
		}

		Documents.beginProgress("Loading revisions...");
		
		revisionsList.clear();
		revisionsList.add(new Label("Loading..."));

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/" + document.getID() + "/revisions/");

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load revisions\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					revisionsList.clear();

					if (200 == response.getStatusCode()) {
						JSONArray jsonRevisions = JSONParser.parse(response.getText()).isArray();
						int size = jsonRevisions.size();

						for (int i = 0; i < size; i++) {							
							String revisionID = jsonRevisions.get(i).isString().stringValue();
							if (i == 0) {
								revisionsList.add(new Hyperlink(revisionID + " (latest)", document.getID() + "/revisions/" + revisionID));							
							} else {
								revisionsList.add(new Hyperlink(revisionID, document.getID() + "/revisions/" + revisionID));							
							}
						}

						spaceLastWidgetInPanel(revisionsList);

						Documents.endProgressWithAlert(null);
					} else {
						Documents.endProgressWithAlert("Couldn't load revisions (" + response.getStatusText() + ")");
					}
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load revisions\n\n" + e);
		}
		return null;
	}	
}

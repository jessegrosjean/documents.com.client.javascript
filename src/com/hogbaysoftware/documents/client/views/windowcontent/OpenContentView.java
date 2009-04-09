package com.hogbaysoftware.documents.client.views.windowcontent;

import java.util.ArrayList;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class OpenContentView extends ContentView {
	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel documentsList = new VerticalPanel();

	public OpenContentView() {
		initWidget(scrollPanel);
		scrollPanel.add(documentsList);
	}

	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath("Open", "open");
	}

	public Request refreshFromServer() {
		documentsList.clear();

		Document.refreshDocumentsFromServer(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}
			
			public void onResponseReceived(Request request, Response response) {
				ArrayList<Document> documents = Document.getDocuments();
				if (documents.size() == 0) {
					documentsList.add(new Label("Your account has no saved documents."));
				} else {
					for (Document each : documents) {
						documentsList.add(new Hyperlink(each.getName(), each.getID()));
					}
				}
				
				spaceLastWidgetInPanel(documentsList);
			}
		});
		
		return null;
	}
}

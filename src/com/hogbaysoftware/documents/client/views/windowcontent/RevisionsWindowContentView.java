package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionsWindowContentView extends WindowContentView {
	private FlowPanel revisionsViewPanel = new FlowPanel();
	
	public RevisionsWindowContentView() {
		initWidget(revisionsViewPanel);
	}
		
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getWindowView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions");
		//Documents.getSharedInstance().setWindowTitle(Documents.getSharedInstance().getDocument().getDisplayName() + " > Revisions", null);
	}
	
	public Request refreshFromServer() {
		revisionsViewPanel.clear();
		
		Document document = Documents.getSharedInstance().getDocument();
		int version = document.getVersion();
		
		if (version == -1) {
			revisionsViewPanel.add(new Label("This document has no saved versions."));
			return null;
		}
		
		String baseHistoryToken = document.getID() + "/revisions/";
		Hyperlink revisionLink;
		
		for (int i = 0; i <= version; i++) {
			if (i == version) {
				revisionLink = new Hyperlink(version + " (current)", baseHistoryToken + i);
			} else {
				revisionLink = new Hyperlink(Integer.toString(i), baseHistoryToken + i);
			}
			revisionLink.addStyleName("revisionLink");
			revisionsViewPanel.add(revisionLink);
		}

		return null;
	}
	
}

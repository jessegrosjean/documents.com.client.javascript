package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionsContentView extends ContentView {
	private ScrollPanel scrollPanel = new ScrollPanel();
	private HTML linksHTML = new HTML();
	
	public RevisionsContentView() {
		initWidget(scrollPanel);
		linksHTML.addStyleName("scrolled-content");
		linksHTML.addStyleName("revisionLinks");
		scrollPanel.add(linksHTML);
	}
		
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions");
	}
	
	public Request refreshFromServer() {
		linksHTML.setHTML("");
		
		Document document = Documents.getSharedInstance().getDocument();
		int version = document.getVersion();
		
		if (version == -1) {
			linksHTML.setHTML("This document has not been saved yet.");
			return null;
		}
		
		String baseHistoryToken = "#" + document.getID() + "/revisions/";
		StringBuffer links = new StringBuffer();
		
		for (int i = 0; i <= version; i++) {
			if (i == version) {
				links.append("<a href=\"" + baseHistoryToken + i + "\">" + version + " (current)</a>");
			} else {
				links.append("<a href=\"" + baseHistoryToken + i + "\">" + Integer.toString(i) + "</a> ");
			}
		}

		linksHTML.setHTML(links.toString());

		return null;
	}
	
}

package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class HistoryWindowContentView extends WindowContentView {
	private VerticalPanel historyViewPanel = new VerticalPanel();
	private VerticalPanel historyList = new VerticalPanel();
	private Document document;
	
	public HistoryWindowContentView() {
		historyViewPanel.add(historyList);
		initWidget(historyViewPanel);
	}
	
	public Document getDocument() {
		return document;
	}
	
	public void setDocument(Document aDocument) {
		document = aDocument;
	}
	
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().setWindowTitle(document.getDisplayName() + " > History");
	}
	
}

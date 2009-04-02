package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;

public class HistoryWindowContentView extends WindowContentView {
	private VerticalPanel historyViewPanel = new VerticalPanel();
	private VerticalPanel historyList = new VerticalPanel();
	
	public HistoryWindowContentView() {
		historyViewPanel.add(historyList);
		initWidget(historyViewPanel);
	}
		
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().setWindowTitle(Documents.getSharedInstance().getDocument().getDisplayName() + " > History", null);
	}
	
}

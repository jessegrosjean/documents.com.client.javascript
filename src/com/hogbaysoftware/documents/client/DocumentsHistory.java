package com.hogbaysoftware.documents.client;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.views.windowcontent.ConflictsWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.DocumentWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.HelpWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.HistoryWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.OpenWindowContentView;

public class DocumentsHistory implements HistoryListener {
	
	private OpenWindowContentView documentsView = new OpenWindowContentView();
	private DocumentWindowContentView documentView = new DocumentWindowContentView();
	private HistoryWindowContentView historyView = new HistoryWindowContentView();
	private ConflictsWindowContentView conflictsView = new ConflictsWindowContentView();
	private HelpWindowContentView helpView = new HelpWindowContentView();
	
	public DocumentsHistory() {
		History.addHistoryListener(this);
	}
	
	public void onHistoryChanged(String historyToken) {
		if (historyToken == "new") {
			documentView.setDocument(new Document());
			Documents.getSharedInstance().setCurrentWindowContentView(documentView);		
		} else if (historyToken == "open") {
			Documents.getSharedInstance().setCurrentWindowContentView(documentsView);			
		} else if (historyToken == "conflicts") {
			Documents.getSharedInstance().setCurrentWindowContentView(conflictsView);			
		} else if (historyToken == "help") {
			Documents.getSharedInstance().setCurrentWindowContentView(helpView);			
		} else {
			String[] historyTokens = historyToken.split("/");
			
			if (historyTokens.length == 1) {
				Document document = Document.getDocumentForID(historyTokens[0]);
				documentView.setDocument(document);
				Documents.getSharedInstance().setCurrentWindowContentView(documentView);							
			} else if (historyTokens[1].equalsIgnoreCase("history")) {
				Document document = Document.getDocumentForID(historyTokens[0]);
				historyView.setDocument(document);
				Documents.getSharedInstance().setCurrentWindowContentView(historyView);							
			}
		}
	}
}

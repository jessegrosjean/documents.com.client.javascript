package com.hogbaysoftware.documents.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.hogbaysoftware.documents.client.model.Document;

public class DocumentsHistory implements ValueChangeHandler<String> {
		
	public DocumentsHistory() {
		History.addValueChangeHandler(this);
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		Documents documents = Documents.getSharedInstance();
		String[] historyTokens = event.getValue().split("/");
		
		if (historyTokens[0].equalsIgnoreCase("new") || historyTokens[0].equalsIgnoreCase("")) {
			documents.newAction();
		} else if (historyTokens[0].equalsIgnoreCase("open")) {
			documents.openAction();
		} else if (historyTokens[0].equalsIgnoreCase("conflicts")) {
			documents.showConflicts();
		} else {
			if (historyTokens.length == 1) {
				documents.openDocument(Document.getDocumentForID(historyTokens[0], false));
			} else if (historyTokens[1].equalsIgnoreCase("revisions")) {
				if (historyTokens.length == 3) {
					documents.showRevision(Document.getDocumentForID(historyTokens[0], false), historyTokens[2]);
				} else {
					documents.showRevisions(Document.getDocumentForID(historyTokens[0], false));
				}
			} else if (historyTokens[1].equalsIgnoreCase("sharing")) {
				documents.showSharing(Document.getDocumentForID(historyTokens[0], false));
			}
		}
	}
}

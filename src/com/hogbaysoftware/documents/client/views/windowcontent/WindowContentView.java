package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.Composite;

public class WindowContentView extends Composite {
	private Request refreshFromServerRequest;
	
/*	public void newAction() {
		Documents.getSharedInstance().newAction();
	}

	public void openAction() {
		Documents.getSharedInstance().openAction();
	}
	
	public void openDocument(Document document) {
	}
	
	public Request saveAction() {
		return Documents.getSharedInstance().saveAction();
	}

	public Request deleteAction() {
		return Documents.getSharedInstance().deleteAction();
	}

	public void renameAction() {
		Documents.getSharedInstance().renameAction();
	}

	public void showSharing(Document document) {
		Documents.getSharedInstance().showSharing(document);
	}
	
	public void showHistory(Document document) {
		Documents.getSharedInstance().showHistory(document);
	}
	
	public void showConflicts() {
		Documents.getSharedInstance().showConflicts();
	}
	
	public void showHelp() {
		Documents.getSharedInstance().showHelp();
	}*/
	
	public boolean hasEdits() {
		return false;
	}	
	
	public void commitEdits() {
	}
	
	public void viewDidShow() {
		refreshFromServerRequest = refreshFromServer();
	}
	
	public void viewDidHide() {
		if (refreshFromServerRequest != null) {
			refreshFromServerRequest.cancel();
		}
		refreshFromServerRequest = null;
	}
		
	public Request refreshFromServer() {
		return null;
	}
}

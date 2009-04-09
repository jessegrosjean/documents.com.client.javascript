package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentView extends Composite {
	private Request refreshFromServerRequest;
	
	public ContentView() {
	}
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
	
	public void spaceLastWidgetInPanel(VerticalPanel panel) {
		if (panel.getWidgetCount() > 0) {
			Widget w = panel.getWidget(panel.getWidgetCount() - 1);
			panel.setCellHeight(w, "100%");
			panel.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_TOP);
		}
	}
	
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

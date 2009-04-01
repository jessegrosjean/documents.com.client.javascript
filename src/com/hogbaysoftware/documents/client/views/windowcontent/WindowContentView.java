package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.Composite;

public class WindowContentView extends Composite {
	private Request refreshRequest;
		
	public void newAction() {
	}

	public void openAction() {
	}

	public void openAction(String documentID) {
	}
	
	public Request saveAction() {
		return null;
	}

	public Request deleteAction() {
		return null;
	}

	public void renameAction() {
	}

	public void showSharing() {
	}
	
	public void showHistory() {
	}
	
	public void showConflicts() {
	}
	
	public void showHelp() {
	}
	
	public void viewDidShow() {
		refreshRequest = refresh();
	}
	
	public void viewDidHide() {
		if (refreshRequest != null) {
			refreshRequest.cancel();
		}
		refreshRequest = null;
	}
	
	public Request refresh() {
		return null;
	}
}

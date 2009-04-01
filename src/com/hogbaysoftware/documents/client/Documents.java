package com.hogbaysoftware.documents.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.hogbaysoftware.documents.client.views.MenuView;
import com.hogbaysoftware.documents.client.views.WindowView;
import com.hogbaysoftware.documents.client.views.windowcontent.WindowContentView;

public class Documents implements EntryPoint, WindowResizeListener {
	public static Documents sharedInstance;

	private MenuView menuView = new MenuView();
	private WindowView windowView = new WindowView();
	private Label statusView = new Label();

	public static Documents getSharedInstance() {
		return sharedInstance;
	}

	public static String formatServiceURL(String url) {
		if (GWT.isScript()) {
			return "/v1/" + url;
		} else {
			return GWT.getModuleBaseURL() + "v1/" + url + ".json";
		}
	}
	
	public void onModuleLoad() {
		sharedInstance = this;

		new DocumentsHistory();

		RootPanel.get("desktop").add(menuView);
		RootPanel.get("desktop").add(windowView);
		RootPanel.get("status").add(statusView);
		
		newAction();
		
		Window.addWindowResizeListener(this);
		onWindowResized(Window.getClientWidth(), Window.getClientHeight());
	}

	public void onWindowResized(int width, int height) {
		windowView.setHeight((height - windowView.getAbsoluteTop()) + "px");
	}
	
	//
	// Actions
	//
	
	public void newAction() {
		History.newItem("new");
	}

	public void openAction() {
		History.newItem("open");
	}
	
	public void saveAction() {
		getCurrentWindowContentView().saveAction();
	}

	public void deleteAction() {
		getCurrentWindowContentView().deleteAction();
	}

	public void renameAction() {
		getCurrentWindowContentView().renameAction();
	}

	public void showSharing() {
	}
	
	public void showHistory() {
		getCurrentWindowContentView().showHistory();
	}
	
	public void showConflicts() {
		History.newItem("conflicts");
	}
	
	public void showHelp() {
		History.newItem("help");
	}
	
	//
	// Status
	//
	
	public static void beginProgress(String status) {
		Label statusView = Documents.getSharedInstance().statusView;
		if (status == null) status = "";
		statusView.setText(status);
		Window.setStatus(status);
	}
	
	public static void endProgressWithAlert(String alert) {
		Label statusView = Documents.getSharedInstance().statusView;
		statusView.setText("");
		Window.setStatus("");
		if (alert != null) {
			Window.alert(alert);
		}
	}
	
	public void setWindowTitle(String title) {
		windowView.setWindowTitle(title);
	}
	
	//
	// Current Window Content View
	//
	
	public WindowContentView getCurrentWindowContentView() {
		return windowView.getWindowContentView();
	}
	
	public void setCurrentWindowContentView(WindowContentView aWindowContentView) {
		windowView.setWindowContentView(aWindowContentView);
	}
}

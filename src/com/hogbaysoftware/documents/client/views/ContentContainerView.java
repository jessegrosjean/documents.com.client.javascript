package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.views.windowcontent.ContentView;

public class ContentContainerView extends Composite {
	private VerticalPanel contentContainerPanel = new VerticalPanel();
	private ContentView windowContentView;
	
	public ContentContainerView() {
		initWidget(contentContainerPanel);
		contentContainerPanel.addStyleName("content-container");
	}
		
	public ContentView getWindowContentView() {
		return windowContentView;
	}
	
	public void setWindowContentView(ContentView aWindowContentView) {
		if (windowContentView != null) {
			windowContentView.removeStyleName("content");
			contentContainerPanel.remove(windowContentView);
			windowContentView.viewDidHide();
		}
		windowContentView = aWindowContentView;
		if (windowContentView != null) {
			windowContentView.addStyleName("content");

			contentContainerPanel.add(windowContentView);
			contentContainerPanel.setCellWidth(windowContentView, "100%");
			contentContainerPanel.setCellHeight(windowContentView, "100%");
			windowContentView.viewDidShow();
		}
	}
}

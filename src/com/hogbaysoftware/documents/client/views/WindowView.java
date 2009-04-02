package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.views.windowcontent.WindowContentView;

public class WindowView extends Composite {
	private VerticalPanel windowPanel = new VerticalPanel();
	private HorizontalPanel titleBarPanel = new HorizontalPanel();
	private Label titleLabel = new Label();
	private ClickListener titleClickListener;
	private VerticalPanel windowContentFrame = new VerticalPanel();
	private WindowContentView windowContentView;
	
	public WindowView() {
		initWidget(windowPanel);
		
		windowPanel.addStyleName("window");
		titleBarPanel.addStyleName("window-title-bar");
		titleLabel.addStyleName("window-title");

		titleBarPanel.add(titleLabel);
		titleBarPanel.setCellHorizontalAlignment(titleLabel, HorizontalPanel.ALIGN_CENTER);
		
		windowPanel.add(titleBarPanel);
		windowPanel.setCellHeight(titleBarPanel, "0");

		windowContentFrame.addStyleName("window-content-frame");
		windowPanel.add(windowContentFrame);
		windowPanel.setCellHeight(windowContentFrame, "100%");
		windowPanel.setCellWidth(windowContentFrame, "100%");
	}
		
	public WindowContentView getWindowContentView() {
		return windowContentView;
	}

	public void setWindowTitle(String title, ClickListener clickLister) {
		Document document = Documents.getSharedInstance().getDocument();
		if (document != null && document.hasEdits()) {
			title = "◆ " + title;
		}
		
		if (clickLister != titleClickListener) {
			if (titleClickListener != null) {
				titleLabel.removeClickListener(titleClickListener);
				titleLabel.removeStyleName("menuItem");
			}
			titleClickListener = clickLister;
			if (titleClickListener != null) {
				titleLabel.addClickListener(titleClickListener);
				titleLabel.addStyleName("menuItem");
			}
		}
		
		titleLabel.setText(title);
	}
	
	public void setWindowContentView(WindowContentView aWindowContentView) {
		if (windowContentView != null) {
			windowContentView.removeStyleName("window-content");
			windowContentFrame.remove(windowContentView);
			windowContentView.viewDidHide();
		}
		windowContentView = aWindowContentView;
		if (windowContentView != null) {
			windowContentView.addStyleName("window-content");
			windowContentFrame.add(windowContentView);
			windowContentFrame.setCellWidth(windowContentView, "100%");
			windowContentFrame.setCellHeight(windowContentView, "100%");
			windowContentView.viewDidShow();
		}
	}
}

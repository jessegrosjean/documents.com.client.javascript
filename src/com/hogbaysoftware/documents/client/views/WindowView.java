package com.hogbaysoftware.documents.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.views.windowcontent.WindowContentView;

public class WindowView extends Composite {
	private VerticalPanel windowPanel = new VerticalPanel();
	private HorizontalPanel titleBarPanel = new HorizontalPanel();
	private HorizontalPanel titlePanel = new HorizontalPanel();
	
	//private Label titleLabel = new Label();
	//private ClickListener titleClickListener;
	private VerticalPanel windowContentFrame = new VerticalPanel();
	private WindowContentView windowContentView;
	
	public WindowView() {
		initWidget(windowPanel);
		
		windowPanel.addStyleName("window");
		titleBarPanel.addStyleName("window-title-bar");
		titlePanel.addStyleName("window-title");

		titleBarPanel.add(titlePanel);
		titleBarPanel.setCellHorizontalAlignment(titlePanel, HorizontalPanel.ALIGN_CENTER);
		
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
/*
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
*/
	public void setWindowTitlePath(String pathComponent, Object pathAction) {
		ArrayList<String> pathComponents = new ArrayList<String>();
		ArrayList<Object> pathActions = new ArrayList<Object>();
		pathComponents.add(pathComponent);
		pathActions.add(pathAction);
		setWindowTitlePath(pathComponents, pathActions);
	}

	public void setWindowTitlePath(String pathComponent1, Object pathAction1, String pathComponent2, Object pathAction2) {
		ArrayList<String> pathComponents = new ArrayList<String>();
		ArrayList<Object> pathActions = new ArrayList<Object>();
		pathComponents.add(pathComponent1);
		pathActions.add(pathAction1);
		pathComponents.add(pathComponent2);
		pathActions.add(pathAction2);
		setWindowTitlePath(pathComponents, pathActions);
	}

	public void setWindowTitlePath(String pathComponent1, Object pathAction1, String pathComponent2, Object pathAction2, String pathComponent3, Object pathAction3) {
		ArrayList<String> pathComponents = new ArrayList<String>();
		ArrayList<Object> pathActions = new ArrayList<Object>();
		pathComponents.add(pathComponent1);
		pathActions.add(pathAction1);
		pathComponents.add(pathComponent2);
		pathActions.add(pathAction2);
		pathComponents.add(pathComponent3);
		pathActions.add(pathAction3);
		setWindowTitlePath(pathComponents, pathActions);
	}

	public void setWindowTitlePath(List<String> pathComponents, List<Object> pathActions) {
		titlePanel.clear();
		
		Iterator<String> i1 = pathComponents.iterator();
		Iterator<Object> i2 = pathActions.iterator();
		boolean first = true;
		
		while (i1.hasNext()) {
			String pathComponent = i1.next();
			Object pathAction = i2.next();
			
			if (first) {
				Document document = Documents.getSharedInstance().getDocument();
				if (document != null && document.hasEdits()) {
					pathComponent = "◆ " + pathComponent;
				}				
				first = false;
			}
			
			if (pathAction instanceof ClickListener) {
				titlePanel.add(new MenuItemView(pathComponent, (ClickListener)pathAction));
			} else if (pathAction instanceof String) {
				titlePanel.add(new MenuItemView(pathComponent, (String)pathAction));
			} else {
				titlePanel.add(new Label(pathComponent));
			}
			
			if (i1.hasNext()) {
				titlePanel.add(new HTML("&nbsp;>&nbsp;"));
			}
		}
		
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

package com.hogbaysoftware.documents.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class TitleView extends Composite {
	private HorizontalPanel titleBarPanel = new HorizontalPanel();
	private HorizontalPanel titlePanel = new HorizontalPanel();
	
	public TitleView() {
		initWidget(titleBarPanel);
		titleBarPanel.addStyleName("title-bar");
		titlePanel.addStyleName("title");
		titleBarPanel.add(titlePanel);
		titleBarPanel.setCellHorizontalAlignment(titlePanel, HorizontalPanel.ALIGN_CENTER);
	}
	
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
}

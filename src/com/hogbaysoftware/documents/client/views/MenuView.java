package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class MenuView extends Composite implements ClickListener {
	public Label newItem = MenuView.createMenuItem("New", this);
	public Label openItem = MenuView.createMenuItem("Open", this);
	public Label saveItem = MenuView.createMenuItem("Save", this);
	public Label deleteItem = MenuView.createMenuItem("Delete", this);
	public Label shareItem = MenuView.createMenuItem("Share", this);
	public Label historyItem = MenuView.createMenuItem("History", this);
	public Label conflictsItem = MenuView.createMenuItem("Conflicts", this);
	public Label helpItem = MenuView.createMenuItem("Help", this);
	public Label signOutItem = MenuView.createMenuItem("Sign Out", this);

	private HorizontalPanel mainPanel = new HorizontalPanel();
	private HorizontalPanel leftMenu = new HorizontalPanel();
	private HorizontalPanel rightMenu = new HorizontalPanel();
	
	public static Label createMenuItem(String label, ClickListener clickListener) {
		Label menuItem = new Label(label);
		menuItem.addStyleName("menuItem");
		menuItem.addClickListener(clickListener);
		return menuItem;
	}

	public MenuView() {
		initWidget(mainPanel);
				
		mainPanel.addStyleName("menu");
		
		leftMenu.add(newItem);
		leftMenu.add(openItem);
//		leftMenu.add(documentsItem);
		leftMenu.add(saveItem);
		leftMenu.add(deleteItem);
	//	leftMenu.add(renameItem);
//		leftMenu.add(shareItem);
		leftMenu.add(historyItem);
		leftMenu.add(conflictsItem);
		leftMenu.add(helpItem);

		signOutItem.setTitle("jesse@hogbaysoftware.com");
		rightMenu.add(signOutItem);
		rightMenu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		mainPanel.add(leftMenu);
		mainPanel.add(rightMenu);
		mainPanel.setCellHorizontalAlignment(rightMenu, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	public void validateMenuItems() {
		saveItem.removeStyleName("disabled");
		deleteItem.removeStyleName("disabled");
		shareItem.removeStyleName("disabled");
		historyItem.removeStyleName("disabled");

		Document document = Documents.getSharedInstance().getDocument();
		if (document == null) {
			saveItem.addStyleName("disabled");
			deleteItem.addStyleName("disabled");
			shareItem.addStyleName("disabled");
			historyItem.addStyleName("disabled");
		}
	}
	
	public void onClick(Widget sender) {
		if (sender.getStyleName().contains("disabled")) return;

		Documents documents = Documents.getSharedInstance();
		Document document = documents.getDocument();
		
		if (sender == newItem) {
			History.newItem("new");
		} else if (sender == openItem) {
			History.newItem("open");
		} else if (sender == saveItem) {
			documents.saveAction();
		} else if (sender == deleteItem) {
			documents.deleteAction();
		} else if (sender == shareItem) {
			History.newItem(document.getID() + "/sharing");
		} else if (sender == historyItem) {
			History.newItem(document.getID() + "/history");
		} else if (sender == conflictsItem) {
			History.newItem("conflicts");
		} else if (sender == helpItem) {
			History.newItem("help");
		}
	}
}

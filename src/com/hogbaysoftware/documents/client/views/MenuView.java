package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;

public class MenuView extends Composite implements ClickListener {
	public Label newItem = MenuView.createMenuItem("New", this);
	public Label openItem = MenuView.createMenuItem("Open", this);
	public Label saveItem = MenuView.createMenuItem("Save", this);
	public Label renameItem = MenuView.createMenuItem("Rename", this);
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
		leftMenu.add(saveItem);
		leftMenu.add(renameItem);
//		leftMenu.add(shareItem);
		leftMenu.add(historyItem);
		leftMenu.add(deleteItem);
		leftMenu.add(conflictsItem);
		leftMenu.add(helpItem);

		signOutItem.setTitle("jesse@hogbaysoftware.com");
		rightMenu.add(signOutItem);
		rightMenu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		mainPanel.add(leftMenu);
		mainPanel.add(rightMenu);
		mainPanel.setCellHorizontalAlignment(rightMenu, HasHorizontalAlignment.ALIGN_RIGHT);
		
		disableMenuItem(saveItem);
	}
	
	public void disableMenuItem(Label menuItem) {
		menuItem.addStyleName("disabled");
	}

	public void enableMenuItem(Label menuItem) {
		menuItem.removeStyleName("disabled");
	}

	public void onClick(Widget sender) {
		if (sender.getStyleName().contains("disabled")) return;

		signOutItem.setTitle("jesse@hogbaysoftware.com");
		rightMenu.add(signOutItem);

		if (sender == newItem) {
			Documents.getSharedInstance().newAction();
		} else if (sender == openItem) {
			Documents.getSharedInstance().openAction();
		} else if (sender == saveItem) {
			Documents.getSharedInstance().saveAction();
		} else if (sender == renameItem) {
			Documents.getSharedInstance().renameAction();
		} else if (sender == deleteItem) {
			Documents.getSharedInstance().deleteAction();
		} else if (sender == shareItem) {
			Documents.getSharedInstance().showSharing();
		} else if (sender == historyItem) {
			Documents.getSharedInstance().showHistory();
		} else if (sender == conflictsItem) {
			Documents.getSharedInstance().showConflicts();
		} else if (sender == helpItem) {
			Documents.getSharedInstance().showHelp();
		}
	}
}

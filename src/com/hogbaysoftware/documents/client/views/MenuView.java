package com.hogbaysoftware.documents.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class MenuView extends Composite implements ClickListener {
	public Image logo = new Image(GWT.getModuleBaseURL() + "logo.png");
	private MenuItemView newItem = new MenuItemView("New", "new");
	private MenuItemView openItem = new MenuItemView("Open", "open");
	private MenuItemView saveItem = new MenuItemView("Save", this);
	private MenuItemView deleteItem = new MenuItemView("Delete", this);
	private MenuItemView shareItem = new MenuItemView("Share", "share");
	private MenuItemView revisionsItem = new MenuItemView("Revisions", "revisions");
	private MenuItemView conflictsItem = new MenuItemView("Conflicts", "conflicts");
	private MenuItemView helpItem = new MenuItemView("Help", "help");
	private MenuItemView signOutItem = new MenuItemView("Sign Out", this);
	
//	public Label newItem = MenuView.createMenuItem("New", this);
//	public Label openItem = MenuView.createMenuItem("Open", this);
//	public Label saveItem = MenuView.createMenuItem("Save", this);
//	public Label deleteItem = MenuView.createMenuItem("Delete", this);
//	public Label shareItem = MenuView.createMenuItem("Share", this);
//	public Label revisionsItem = MenuView.createMenuItem("Revisions", this);
//	public Label conflictsItem = MenuView.createMenuItem("Conflicts", this);
//	public Label helpItem = MenuView.createMenuItem("Help", this);
//	public Label signOutItem = MenuView.createMenuItem("Sign Out", this);

	private HorizontalPanel mainPanel = new HorizontalPanel();
	private HorizontalPanel leftMenu = new HorizontalPanel();
	private HorizontalPanel rightMenu = new HorizontalPanel();
	
/*	public static Label createMenuItem(String label, ClickListener clickListener) {
		Label menuItem = new Label(label);
		menuItem.addStyleName("menuItem");
		menuItem.addClickListener(clickListener);
		return menuItem;
	}*/

	public MenuView() {
		initWidget(mainPanel);
				
		mainPanel.addStyleName("menu");
		logo.addStyleName("menuItem");
		logo.addClickListener(this);
		
		leftMenu.add(logo);
		leftMenu.add(newItem);
		leftMenu.add(openItem);
		leftMenu.add(saveItem);
		leftMenu.add(deleteItem);
//		leftMenu.add(shareItem);
		leftMenu.add(revisionsItem);
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
		Document document = Documents.getSharedInstance().getDocument();
		
		if (document == null) {
			saveItem.setEnabled(false);
			deleteItem.setEnabled(false);
			shareItem.setEnabled(false);
			revisionsItem.setEnabled(false);
		} else {
			if (History.getToken().split("/").length == 1) {
				saveItem.setEnabled(true);
				deleteItem.setEnabled(true);
			} else {
				saveItem.setEnabled(false);
				deleteItem.setEnabled(false);
			}
			shareItem.setEnabled(true);
			shareItem.setTargetHistoryToken(document.getID() + "/share");
			revisionsItem.setEnabled(true);
			revisionsItem.setTargetHistoryToken(document.getID() + "/revisions");
		}
	}
	
	public void beginProgress() {
		logo.setUrl(GWT.getModuleBaseURL() + "loading.gif");
	}
	
	public void endProgress() {
		logo.setUrl(GWT.getModuleBaseURL() + "logo.png");
	}
	
	public void onClick(Widget sender) {
		Documents documents = Documents.getSharedInstance();
		
		if (sender == logo) {
			documents.goHome();
		} else if (sender == saveItem) {
			documents.saveAction();
		} else if (sender == deleteItem) {
			documents.deleteAction();
		} else if (sender == signOutItem) {
			documents.signOut();
		}	
	}
}

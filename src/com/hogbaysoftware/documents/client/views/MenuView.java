package com.hogbaysoftware.documents.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
	private MenuItemView signOutItem = new MenuItemView("Sign Out", this);
	private HorizontalPanel menuPanel = new HorizontalPanel();
	
	public MenuView() {
		initWidget(menuPanel);
				
		menuPanel.addStyleName("menu");
		logo.addStyleName("menuItem");
		logo.addClickListener(this);	

		menuPanel.add(logo);
		menuPanel.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
		menuPanel.setCellVerticalAlignment(logo, HasVerticalAlignment.ALIGN_MIDDLE);
		menuPanel.add(newItem);
		menuPanel.add(openItem);
		menuPanel.add(saveItem);
		menuPanel.add(deleteItem);
//		mainPanel.add(shareItem);
		menuPanel.add(revisionsItem);
		menuPanel.add(conflictsItem);

		HTML spacer = new HTML("<div></div>");
		menuPanel.add(spacer);
		menuPanel.setCellWidth(spacer, "100%");
		
		signOutItem.setTitle("jesse@hogbaysoftware.com");
		menuPanel.add(signOutItem);
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
			
			if (document.existsOnServer()) {
				revisionsItem.setEnabled(true);
				revisionsItem.setTargetHistoryToken(document.getID() + "/revisions");
			} else {
				revisionsItem.setEnabled(false);
			}
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

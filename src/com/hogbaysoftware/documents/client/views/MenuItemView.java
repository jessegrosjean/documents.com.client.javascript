package com.hogbaysoftware.documents.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;

public class MenuItemView extends Widget implements HasHTML, ClickHandler {
	private String targetHistoryToken;

	public MenuItemView() {
		setElement(DOM.createDiv());
		setStyleName("menuItem");
		addClickHandler(this);
	}

	public MenuItemView(String text, ClickHandler handler) {
		this();
		setText(text);
		addClickHandler(handler);
	}

	public MenuItemView(String text, boolean asHTML, String targetHistoryToken) {
		this();
		if (asHTML) {
			setHTML(text);
		} else {
			setText(text);
		}
		setTargetHistoryToken(targetHistoryToken);
	}

	public MenuItemView(String text, String targetHistoryToken) {
		this();
		setText(text);
		setTargetHistoryToken(targetHistoryToken);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public String getHTML() {
		return DOM.getInnerHTML(getElement());
	}

	public String getTargetHistoryToken() {
		return targetHistoryToken;
	}

	public String getText() {
		return DOM.getInnerText(getElement());
	}

	public void onClick(ClickEvent event) {
		if (getEnabled()) {
			if (targetHistoryToken != null) {
				if (History.getToken().equalsIgnoreCase(targetHistoryToken)) {
					History.fireCurrentHistoryState();
				} else {
					History.newItem(targetHistoryToken);
				}
				event.preventDefault();
			}
		}
	}

	public void setHTML(String html) {
		DOM.setInnerHTML(getElement(), html);
	}

	public void setTargetHistoryToken(String targetHistoryToken) {
		this.targetHistoryToken = targetHistoryToken;
	}

	public void setText(String text) {
		DOM.setInnerText(getElement(), text);
	}

	public boolean getEnabled() {
		return !getStyleName().contains("disabled");
	}

	public void setEnabled(boolean enabled) {
		if (getEnabled() != enabled) {
			if (enabled) {
				removeStyleName("disabled");
			} else {
				addStyleName("disabled");
			}
		}
	}
}

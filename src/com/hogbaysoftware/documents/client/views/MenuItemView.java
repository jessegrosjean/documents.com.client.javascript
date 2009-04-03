package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

public class MenuItemView extends Widget implements HasHTML, SourcesClickEvents {
	//private Element anchorElem;
	private ClickListenerCollection clickListeners;
	private String targetHistoryToken;

	public MenuItemView() {
		setElement(DOM.createDiv());
		//DOM.appendChild(getElement(), anchorElem = DOM.createAnchor());
		sinkEvents(Event.ONCLICK);
		setStyleName("menuItem");
	}

	public MenuItemView(String text, ClickListener clickListener) {
		this();
		setText(text);
		addClickListener(clickListener);
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

	public void addClickListener(ClickListener listener) {
		if (clickListeners == null) {
			clickListeners = new ClickListenerCollection();
		}
		clickListeners.add(listener);
	}

	public String getHTML() {
		return DOM.getInnerHTML(getElement());
		//return DOM.getInnerHTML(anchorElem);
	}

	public String getTargetHistoryToken() {
		return targetHistoryToken;
	}

	public String getText() {
		return DOM.getInnerText(getElement());
		//return DOM.getInnerText(anchorElem);
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (getEnabled()) {
			if (DOM.eventGetType(event) == Event.ONCLICK) {
				if (clickListeners != null) {
					clickListeners.fireClick(this);
				}
				
				if (targetHistoryToken != null) {
					if (History.getToken().equalsIgnoreCase(targetHistoryToken)) {
						History.fireCurrentHistoryState();
					} else {
						History.newItem(targetHistoryToken);
					}
				}
				DOM.eventPreventDefault(event);
			}
		}
	}

	public void removeClickListener(ClickListener listener) {
		if (clickListeners != null) {
			clickListeners.remove(listener);
		}
	}

	public void setHTML(String html) {
		DOM.setInnerHTML(getElement(), html);
		//DOM.setInnerHTML(anchorElem, html);
	}

	public void setTargetHistoryToken(String targetHistoryToken) {
		this.targetHistoryToken = targetHistoryToken;
		//DOM.setElementProperty(anchorElem, "href", "#" + targetHistoryToken);
	}

	public void setText(String text) {
		DOM.setInnerText(getElement(), text);
		//DOM.setInnerText(anchorElem, text);
	}

	@Override
	protected void onEnsureDebugId(String baseID) {
		//ensureDebugId(anchorElem, "", baseID);
		ensureDebugId(getElement(), baseID, "wrapper");
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

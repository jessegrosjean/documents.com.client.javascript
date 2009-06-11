package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class DocumentContentView extends ContentView implements ChangeHandler, KeyDownHandler, KeyUpHandler, ClickHandler {
	private TextArea textArea = new TextArea();

	public DocumentContentView() {
		initWidget(textArea);
		textArea.addChangeHandler(this);
		textArea.addKeyDownHandler(this);
		textArea.addKeyUpHandler(this);
	}

	@Override
	public void viewDidShow() {
		super.viewDidShow();
		refreshFromModel();
	}

	// Change Listeners
	
	public void onChange(ChangeEvent event) {
		commitEdits();
	}
	
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		if (keyCode == KeyCodes.KEY_TAB) {
			textArea.cancelKey();
			int cursorPos = textArea.getCursorPos();
			int selectionLength = textArea.getSelectionLength();
			String text = textArea.getText();
			String newText = text.substring(0, cursorPos) + "\t" + text.substring(cursorPos + selectionLength, text.length());
			textArea.setText(newText);
			textArea.setCursorPos(cursorPos + 1);
		} else if (keyCode == KeyCodes.KEY_ENTER) {
		}
	}
	
	public void onKeyUp(KeyUpEvent event) {
		commitEdits();
	}
	
	public void onClick(ClickEvent event) {
		Document document = Documents.getSharedInstance().getDocument();
		String name = Window.prompt("Rename Document:", document.getDisplayName());
		if (name != null) {
			document.setName(name);
			document.setHasEdits(true);
			refreshWindowTitlePath();
		}
	}

	//
	// Refresh View
	//
	
	public boolean hasEdits() {
		Document document = Documents.getSharedInstance().getDocument();
		return document != null && !document.getContent().equals(textArea.getText());
	}
	
	public void commitEdits() {
		Document document = Documents.getSharedInstance().getDocument();
		String displayName = document.getDisplayName();
		boolean changedContent = document.setContent(textArea.getText());
		
		if (!document.getDisplayName().equals(displayName) || (changedContent && !document.hasEdits())) {
			document.setHasEdits(true);
			refreshWindowTitlePath();
		}
	}

	public void refreshWindowTitlePath() {
		Documents documents = Documents.getSharedInstance();
		documents.getTitleView().setWindowTitlePath(documents.getDocument().getDisplayName(), this);
	}
	
	public void refreshFromModel() {
		Document document = Documents.getSharedInstance().getDocument();
		if (isAttached()) {
			refreshWindowTitlePath();
		}
		textArea.setText(document.getContent());
		textArea.setReadOnly(false);
		textArea.setFocus(true);
		
		Documents.getSharedInstance().forceResizeContentContainerView();
	}
	
	public Request refreshFromServer() {
		Document document = Documents.getSharedInstance().getDocument();
		if (!document.existsOnServer()) {
			return null;
		}

		Documents.beginProgress("Loading document...");

		textArea.setText("Loading...");
		textArea.setReadOnly(true);
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/" + document.getID());

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load document\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						try {
							JSONObject jsonDocument = JSONParser.parse(response.getText()).isObject();
							Document document = Document.getDocumentForID(jsonDocument.get("id").isString().stringValue(), true);
							document.setVersion((int) jsonDocument.get("version").isNumber().doubleValue());
							document.setName(jsonDocument.get("name").isString().stringValue());
							document.setContent(jsonDocument.get("content").isString().stringValue());
							document.setHasEdits(false);
							refreshFromModel();
							Documents.endProgressWithAlert(null);
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse document\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't load document (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load document\n\n" + e);
		}
		return null;
	}
}

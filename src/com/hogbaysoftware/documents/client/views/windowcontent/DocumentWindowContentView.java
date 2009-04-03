package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class DocumentWindowContentView extends WindowContentView implements ChangeListener, ClickListener, KeyboardListener {
	private TextArea textArea = new TextArea();

	public DocumentWindowContentView() {
		initWidget(textArea);
		textArea.addChangeListener(this);
		textArea.addKeyboardListener(this);
	}

	@Override
	public void viewDidShow() {
		super.viewDidShow();
		refreshFromModel();
	}

	// Change Listeners
	
	public void onChange(Widget sender) {
		commitEdits();
	}
	
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		if (keyCode == KeyboardListener.KEY_TAB) {
			textArea.cancelKey();
			int cursorPos = textArea.getCursorPos();
			int selectionLength = textArea.getSelectionLength();
			String text = textArea.getText();
			String newText = text.substring(0, cursorPos) + "\t" + text.substring(cursorPos + selectionLength, text.length());
			textArea.setText(newText);
			textArea.setCursorPos(cursorPos + 1);
		} else if (keyCode == KeyboardListener.KEY_ENTER) {
		}
	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		Document document = Documents.getSharedInstance().getDocument();
		if (!document.hasEdits()) {
			if (hasEdits()) {
				document.setHasEdits(true);
				refreshWindowTitlePath();
			}
		}
	}
	
	public void onClick(Widget sender) {
		Document document = Documents.getSharedInstance().getDocument();
		String name = Window.prompt("Name", document.getDisplayName());
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
		document.setContent(textArea.getText());
		if (!document.hasEdits()) {
			document.setHasEdits(true);
			refreshWindowTitlePath();
		}
	}

	public void refreshWindowTitlePath() {
		Documents documents = Documents.getSharedInstance();
		documents.getWindowView().setWindowTitlePath(documents.getDocument().getDisplayName(), this);
	}
	
	public void refreshFromModel() {
		Document document = Documents.getSharedInstance().getDocument();
		if (isAttached()) {
			refreshWindowTitlePath();
		}
		textArea.setText(document.getContent());
		textArea.setFocus(true);		
	}
	
	public Request refreshFromServer() {
		Document document = Documents.getSharedInstance().getDocument();
		if (!document.existsOnServer()) {
			return null;
		}

		Documents.beginProgress("Loading document...");

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

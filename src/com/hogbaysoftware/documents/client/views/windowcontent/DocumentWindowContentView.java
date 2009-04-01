package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.model.DocumentListener;

public class DocumentWindowContentView extends WindowContentView implements DocumentListener {
	private Document document;
	private TextArea textArea = new TextArea();

	public DocumentWindowContentView() {
		initWidget(textArea);

		textArea.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				document.setContent(textArea.getText());
			}
		});

		textArea.addKeyboardListener(new KeyboardListenerAdapter() {
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
		});
	}

	@Override
	public void viewDidShow() {
		super.viewDidShow();
		textArea.setFocus(true);
		onChanged(document);
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document aDocument) {
		if (document != null) {
			document.removeDocumentListener(this);
		}
		document = aDocument;
		if (document != null) {
			document.addDocumentListener(this);
			onChanged(document);
		}
	}

	public void onChanged(Document document) {
		textArea.setText(document.getContent());
		if (isAttached()) {
			Documents.getSharedInstance().setWindowTitle(document.getDisplayName());
		}
	}

	public Request saveAction() {
		Documents.beginProgress("Saving document...");

		RequestBuilder builder = null;
		JSONObject jsonDocument = new JSONObject();

		if (document.existsOnServer()) {
			jsonDocument.put("id", new JSONString(document.getID()));
			jsonDocument.put("version", new JSONNumber(document.getVersion()));
		}

		if (document.getName() != null) jsonDocument.put("name", new JSONString(document.getName()));
		if (document.getContent() != null) jsonDocument.put("content", new JSONString(document.getContent()));

		if (document.existsOnServer()) {
			builder = new RequestBuilder(RequestBuilder.POST, Documents.formatServiceURL("documents/" + document.getID()));
			builder.setHeader("X-HTTP-Method-Override", "PUT");
		} else {
			builder = new RequestBuilder(RequestBuilder.POST, Documents.formatServiceURL("documents/"));
		}

		try {
			return builder.sendRequest(jsonDocument.toString(), new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't save document\n\n" + e);
				}
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode() || 201 == response.getStatusCode()) {
						try {
							JSONObject jsonResponse = JSONParser.parse(response.getText()).isObject();
							document.setVersion((int) jsonResponse.get("version").isNumber().doubleValue());

							if (jsonResponse.get("name") != null) {
								document.setName(jsonResponse.get("name").isString().stringValue());
							}

							if (jsonResponse.get("content") != null) {
								document.setContent(jsonResponse.get("content").isString().stringValue());
							}

							Documents.endProgressWithAlert(null);
							
							if (jsonResponse.get("failed_patches") != null) {
								//@SuppressWarnings("unused")
								//String message = "Some of your most recent changes conflict with changes just made by a collaborator, and have been discarded. This should only affect recent changes. Please note the discarded changes here, and then go back and edit the document again, adding back the changes that are still appropriate.";
							}
							
							textArea.setFocus(true);
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse document\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't save document (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't ave document\n\n" + e);
		}
		return null;
	}

	public Request deleteAction() {
		if (!Window.confirm("Are you sure that you want to delete this document? This action cannot be undone.")) return null;

		Documents.beginProgress("Deleting document...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Documents.formatServiceURL("documents/" + document.getID() + "?version=" + document.getVersion()));
		builder.setHeader("X-HTTP-Method-Override", "DELETE");

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't delete document\n\n" + e);
				}
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						History.newItem("open");
						Documents.endProgressWithAlert(null);
					} else {
						Documents.endProgressWithAlert("Couldn't delete document (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't delete document\n\n" + e);
		}
		return null;
	}

	public void renameAction() {
		String name = Window.prompt("Name", document.getDisplayName());
		if (name != null) {
			document.setName(name);
		}
		textArea.setFocus(true);
	}

	public void showHistory() {
		History.newItem(document.getID() + "/history");
	}

	public Request refresh() {
		if (document.getID() == null || document.getContent() != null) {
			return null;
		}

		Documents.beginProgress("Loading document...");

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, Documents.formatServiceURL("documents/" + document.getID()));

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load document\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						try {
							JSONObject jsonDocument = JSONParser.parse(response.getText()).isObject();
							Document document = Document.getDocumentForID(jsonDocument.get("id").isString().stringValue());
							document.setVersion((int) jsonDocument.get("version").isNumber().doubleValue());
							document.setName(jsonDocument.get("name").isString().stringValue());
							document.setContent(jsonDocument.get("content").isString().stringValue());
							textArea.setFocus(true);
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

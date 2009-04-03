package com.hogbaysoftware.documents.client;


import com.google.gwt.core.client.EntryPoint;
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
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.views.MenuView;
import com.hogbaysoftware.documents.client.views.WindowView;
import com.hogbaysoftware.documents.client.views.windowcontent.ConflictsWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.DocumentWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.HelpWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.OpenWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.RevisionWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.RevisionsWindowContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.WindowContentView;

public class Documents implements EntryPoint, WindowResizeListener {
	public static Documents sharedInstance;
	
	private Document document;
	private MenuView menuView = new MenuView();
	private WindowView windowView = new WindowView();
	private OpenWindowContentView documentsView = new OpenWindowContentView();
	private DocumentWindowContentView documentView = new DocumentWindowContentView();
	private RevisionsWindowContentView revisionsView = new RevisionsWindowContentView();
	private RevisionWindowContentView revisionView = new RevisionWindowContentView();
	private ConflictsWindowContentView conflictsView = new ConflictsWindowContentView();
	private HelpWindowContentView helpView = new HelpWindowContentView();

	public static Documents getSharedInstance() {
		return sharedInstance;
	}
	
	public void onModuleLoad() {
		sharedInstance = this;

		windowView.setWindowTitlePath("Loading...", null);
		
		RootPanel.get("desktop").add(menuView);
		RootPanel.get("desktop").add(windowView);
		
		Window.addWindowResizeListener(Documents.this);
		onWindowResized(Window.getClientWidth(), Window.getClientHeight());

		Document.refreshDocumentsFromServer(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}
			
			public void onResponseReceived(Request request, Response response) {
				new DocumentsHistory();
				
				if (History.getToken().equalsIgnoreCase("")) {
					History.newItem("new");
				} else {
					History.fireCurrentHistoryState();
				}
			}
		});
	}

	public void onWindowResized(int width, int height) {
		windowView.setHeight((height - windowView.getAbsoluteTop()) + "px");
	}
	
	//
	// Actions
	//

	public boolean shouldCancelNavigation() {
		WindowContentView currentWindowContentView = getWindowContentView();
		
		if (document != null && document.hasEdits() || currentWindowContentView != null && currentWindowContentView.hasEdits()) {
			if (!Window.confirm("Your document has unsaved changes. Are you sure that you want to continue and lose those changes?")) {
				return false;
			}
			currentWindowContentView.commitEdits();
			return true;
		}
		return true;
	}
	
	public void newAction() {
		if (!shouldCancelNavigation()) return;
		Document document = Document.getDocumentForID("new", true);
		document.setContent("");
		document.setName(null);
		document.setHasEdits(false);
		openDocument(document);
		//History.newItem(historyToken)
		//setWindowContentView(documentView);
	}

	public void openAction() {
		if (!shouldCancelNavigation()) return;
		setDocument(null);
		setWindowContentView(documentsView);			
	}

	public void openDocument(Document document) {
		if (!shouldCancelNavigation()) return;
		setDocument(document);
		setWindowContentView(documentView);							
	}

	public Request saveAction() {
		RequestBuilder builder = null;
		JSONObject jsonDocument = new JSONObject();
		final boolean initialExistsOnServer = document.existsOnServer();

		if (initialExistsOnServer) {
			jsonDocument.put("id", new JSONString(document.getID()));
			jsonDocument.put("version", new JSONNumber(document.getVersion()));
		} else if (document.getName() == null) {
			String name = Window.prompt("Save Document As:", document.getDisplayName());
			if (name != null) {
				document.setName(name);
			} else {
				return null;
			}
		}

		Documents.beginProgress("Saving document...");

		if (document.getName() != null) jsonDocument.put("name", new JSONString(document.getName()));
		if (document.getContent() != null) jsonDocument.put("content", new JSONString(document.getContent()));

		if (document.existsOnServer()) {
			builder = new RequestBuilder(RequestBuilder.POST, "/v1/documents/" + document.getID());
			builder.setHeader("X-HTTP-Method-Override", "PUT");
		} else {
			builder = new RequestBuilder(RequestBuilder.POST, "/v1/documents/");
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
							Document document = Documents.this.getDocument();
							document.setVersion((int) jsonResponse.get("version").isNumber().doubleValue());

							if (jsonResponse.get("id") != null) {
								document.setID(jsonResponse.get("id").isString().stringValue());
							}
							
							if (jsonResponse.get("name") != null) {
								document.setName(jsonResponse.get("name").isString().stringValue());
							}

							if (jsonResponse.get("content") != null) {
								document.setContent(jsonResponse.get("content").isString().stringValue());
							}

							document.setHasEdits(false);
							
							Documents.endProgressWithAlert(null);

							if (!initialExistsOnServer) {
								History.newItem(document.getID());
							} else {
								documentView.refreshFromModel();
							}

							if (jsonResponse.get("failed_patches") != null) {
								//Window.confirm();
								//@SuppressWarnings("unused")
								//String message = "Some of your most recent changes conflict with changes just made by a collaborator, and have been discarded. This should only affect recent changes. Please note the discarded changes here, and then go back and edit the document again, adding back the changes that are still appropriate.";
							}
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
		if (!Window.confirm("Are you sure that you want to delete the document \"" + document.getDisplayName() + "\"? This action cannot be undone.")) return null;

		if (!document.existsOnServer()) {
			setDocument(null);
			History.newItem("open");
			return null;
		}
		
		Documents.beginProgress("Deleting document...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/v1/documents/" + document.getID() + "?version=" + document.getVersion());
		builder.setHeader("X-HTTP-Method-Override", "DELETE");

		try {
			return builder.sendRequest("", new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't delete document\n\n" + e);
				}
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Documents.endProgressWithAlert(null);
						setDocument(null);
						History.newItem("open");
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
	
	public void showSharing(Document document) {
		if (!shouldCancelNavigation()) return;
		setDocument(document);
	}
	
	public void showRevisions(Document document) {
		if (!shouldCancelNavigation()) return;
		setDocument(document);
		setWindowContentView(revisionsView);							
	}

	public void showRevision(Document document, String revision) {
		if (!shouldCancelNavigation()) return;
		setDocument(document);
		revisionView.setRevision(revision);
		setWindowContentView(revisionView);							
	}

	public void showConflicts() {
		if (!shouldCancelNavigation()) return;
		setDocument(null);
		setWindowContentView(conflictsView);			
	}
	
	public void showHelp() {
		if (!shouldCancelNavigation()) return;
		setDocument(null);
		setWindowContentView(helpView);			
	}
	
	public void goHome() {
		if (!shouldCancelNavigation()) return;
		Window.Location.assign("/");
	}
	
	public void signOut() {
		if (!shouldCancelNavigation()) return;
		
	}
	
	//
	// Document
	//
	
	public Document getDocument() {
		return document;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	//
	// Status
	//
	
	public static void beginProgress(String progressMessage) {
		if (progressMessage == null) progressMessage = "";
		getSharedInstance().menuView.beginProgress();
		Window.setStatus(progressMessage);
	}
	
	public static void endProgressWithAlert(String alert) {
		Documents.getSharedInstance().menuView.endProgress();
		Window.setStatus("");
		if (alert != null) {
			Window.alert(alert);
		}
	}
		
	//
	// Window
	//
	
	public WindowView getWindowView() {
		return windowView;
	}
	
	public WindowContentView getWindowContentView() {
		return windowView.getWindowContentView();
	}
	
	public void setWindowContentView(WindowContentView aWindowContentView) {
		windowView.setWindowContentView(aWindowContentView);
		menuView.validateMenuItems();
	}
}

package com.hogbaysoftware.documents.client;


import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.hogbaysoftware.documents.client.model.Document;
import com.hogbaysoftware.documents.client.views.ContentContainerView;
import com.hogbaysoftware.documents.client.views.MenuView;
import com.hogbaysoftware.documents.client.views.TitleView;
import com.hogbaysoftware.documents.client.views.windowcontent.ConflictsContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.ContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.DocumentContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.OpenContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.RevisionContentView;
import com.hogbaysoftware.documents.client.views.windowcontent.RevisionsContentView;

public class Documents implements EntryPoint, NativePreviewHandler, ResizeHandler {
	public static Documents sharedInstance;
	private static ArrayList<String> progressMessageStack = new ArrayList<String>();

	private Document document;
	private MenuView menuView;
	private TitleView titleView;
	private ContentContainerView contentContainerView;
	private OpenContentView documentsView;
	private DocumentContentView documentView;
	private RevisionsContentView revisionsView;
	private RevisionContentView revisionView;
	private ConflictsContentView conflictsView;
	private String serviceName;
	private boolean isIPhoneHosted;
	
	public static Documents getSharedInstance() {
		return sharedInstance;
	}

	public String serviceName() {
		return serviceName;
	}
	
	public boolean isIPhoneHosted() {
		return isIPhoneHosted;
	}
	
	public void onModuleLoad() {
		sharedInstance = this;
		serviceName = RootPanel.get("service.name").getElement().getInnerText();
		isIPhoneHosted = RootPanel.get("iphone.server") != null;
		menuView = new MenuView();
		titleView = new TitleView();
		contentContainerView = new ContentContainerView();
		documentsView = new OpenContentView();
		documentView = new DocumentContentView();
		revisionsView = new RevisionsContentView();
		revisionView = new RevisionContentView();
		conflictsView = new ConflictsContentView();
		
		titleView.setWindowTitlePath("Loading...", null);
		
		RootPanel.get("desktop").getElement().setInnerHTML("");		
		RootPanel.get("desktop").add(menuView);
		RootPanel.get("desktop").add(titleView);
		RootPanel.get("desktop").add(contentContainerView);

		Window.addResizeHandler(this);
		Window.enableScrolling(false);
		Documents.registerForOnBeforeUnload(this);
		Event.addNativePreviewHandler(this);

		Document.refreshDocumentsFromServer(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}

			public void onResponseReceived(Request request, Response response) {
				new DocumentsHistory();

				if (History.getToken().equalsIgnoreCase("")) {
					if (Document.getDocuments().size() == 0) {
						History.newItem("new");
					} else {
						History.newItem("open");
					}
				} else {
					History.fireCurrentHistoryState();
				}
			}
		});
	}

	public void onPreviewNativeEvent(NativePreviewEvent event) {		
		int type = event.getTypeInt();
		if (type == Event.ONKEYDOWN) {
			NativeEvent nativeEvent = event.getNativeEvent();
			char keyCode = (char) nativeEvent.getKeyCode();
			boolean ctrl = nativeEvent.getCtrlKey();
			boolean meta = nativeEvent.getMetaKey();
			boolean shortcut = (ctrl || meta);

			if (shortcut) {
				if  (getWindowContentView() == documentView && keyCode == 'S') {
					saveAction();
					event.cancel();
				} else if  (keyCode == 'O') {
					History.newItem("open");
					event.cancel();
				} else if  (keyCode == 'N') {
					History.newItem("new");
					event.cancel();
				}
			}
		}
	}

	public void onResize(ResizeEvent event) {
		contentContainerView.setHeight((event.getHeight() - (contentContainerView.getAbsoluteTop())) + "px");
	}
	
	//
	// Actions
	//

	public String shouldCancelNavigationConfirmMessage() {
		return "Your document has unsaved changes. If you leave this page without saving you will lose those changes.";
	}

	public boolean shouldCancelNavigation() {
		return shouldCancelNavigation(false);
	}

	public boolean shouldCancelNavigation(boolean displayConfirm) {
		ContentView currentWindowContentView = getWindowContentView();

		if (document != null && document.hasEdits() || currentWindowContentView != null && currentWindowContentView.hasEdits()) {
			if (displayConfirm) {
				if (!Window.confirm("Are you sure you want to leave this page?\n\n" + shouldCancelNavigationConfirmMessage() + "\n\nClick OK to continue, or Cancel to stay on this page.")) {
					return true;
				} else {
					return false;
				}
			}
			currentWindowContentView.commitEdits();
			return true;
		}
		return false;
	}

	public void newAction() {
		if (shouldCancelNavigation(true)) return;
		Document document = Document.getDocumentForID("new", true);
		document.setContent("");
		document.setName(null);
		document.setHasEdits(false);
		openDocument(document);
	}

	public void openAction() {
		if (shouldCancelNavigation(true)) return;
		setDocument(null);
		setWindowContentView(documentsView);			
	}

	public void openDocument(Document document) {
		if (shouldCancelNavigation(true)) return;
		setDocument(document);
		setWindowContentView(documentView);							
	}

	public Request saveAction() {
		if (!document.hasEdits()) {
			return null;
		}
		
		RequestBuilder builder = null;
		JSONObject jsonDocument = new JSONObject();
		final boolean initialExistsOnServer = document.existsOnServer();

		if (initialExistsOnServer) {
			jsonDocument.put("id", new JSONString(document.getID()));
			jsonDocument.put("version", new JSONNumber(document.getVersion()));
		}

		Documents.beginProgress("Saving document...");

		getWindowContentView().commitEdits();

		document.setName(document.getDisplayName());

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

							if (jsonResponse.get("conflicts") != null) {
								if (Window.confirm("Some of your edits conflict with recent changes made on " + serviceName() + ". To resolve those conflicts now click the OK button.")) {
									History.newItem("conflicts");
								}
							}
						} catch (JSONException e) {
							Documents.endProgressWithAlert("Couldn't parse document\n\n" + e);
						}
					} else {
						Documents.endProgressWithAlert("Couldn't save document (" + response.getStatusText() + ")\n" + response.getText());
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
		if (shouldCancelNavigation(true)) return;
		setDocument(document);
	}

	public void showRevisions(Document document) {
		if (shouldCancelNavigation(true)) return;
		setDocument(document);
		setWindowContentView(revisionsView);							
	}

	public void showRevision(Document document, String revision) {
		if (shouldCancelNavigation(true)) return;
		setDocument(document);
		revisionView.setRevision(revision);
		setWindowContentView(revisionView);							
	}

	public void showConflicts() {
		if (shouldCancelNavigation(true)) return;
		setDocument(null);
		setWindowContentView(conflictsView);			
	}

	public void goHome() {
		Window.Location.assign("/");
	}

	public void signOut() {
		Window.Location.assign(DOM.getElementById("logout_url").getAttribute("content"));
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
		progressMessageStack.add(progressMessage);
		Window.setStatus(progressMessage);
		if (progressMessageStack.size() == 1) {
			getSharedInstance().menuView.beginProgress();
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "progress");
		}
	}

	public static void endProgressWithAlert(String alert) {
		progressMessageStack.remove(progressMessageStack.size() - 1);
		if (progressMessageStack.size() == 0) {
			Documents.getSharedInstance().menuView.endProgress();
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "");			
			Window.setStatus("");
		} else {
			Window.setStatus(progressMessageStack.get(progressMessageStack.size() - 1));
		}
		if (alert != null) {
			Window.alert(alert);
		}
	}

	//
	// Window
	//

	public TitleView getTitleView() {
		return titleView;
	}

	public ContentView getWindowContentView() {
		return contentContainerView.getWindowContentView();
	}

	public void setWindowContentView(ContentView aWindowContentView) {
		contentContainerView.setWindowContentView(aWindowContentView);
		menuView.validateMenuItems();
	}

	public void forceResizeContentContainerView() {
		contentContainerView.setHeight((Window.getClientHeight() - (contentContainerView.getAbsoluteTop())) + "px");
	}
	
	private native static String registerForOnBeforeUnload(Documents documents) /*-{
		$wnd.onbeforeunload = function() {
			var shouldCancelNavigation = documents.@com.hogbaysoftware.documents.client.Documents::shouldCancelNavigation()();
			if (shouldCancelNavigation == true) {
				return documents.@com.hogbaysoftware.documents.client.Documents::shouldCancelNavigationConfirmMessage()();
			}
		};
	}-*/;

}

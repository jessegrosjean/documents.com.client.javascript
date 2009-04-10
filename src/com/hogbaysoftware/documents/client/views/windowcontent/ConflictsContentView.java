package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.views.ConflictView;

public class ConflictsContentView extends ContentView {
	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel conflictsList = new VerticalPanel();

	public ConflictsContentView() {
		initWidget(scrollPanel);
		conflictsList.setWidth("100%");
		conflictsList.addStyleName("scrolled-content");
		scrollPanel.add(conflictsList);		
	}
	
	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath("Conflicts", "conflicts");
	}

	public Request refreshFromServer() {
		Documents.beginProgress("Loading conflicts...");
		
		conflictsList.clear();
		conflictsList.add(new Label("Loading..."));
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/conflicts");

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load conflicts\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					conflictsList.clear();
					
					if (200 == response.getStatusCode()) {
						JSONArray jsonConflicts = JSONParser.parse(response.getText()).isArray();
						int size = jsonConflicts.size();

						if (size > 0) {
							conflictsList.add(new HTML("<p><strong>Your account has sync conflicts.</strong></p>"));
							conflictsList.add(new HTML("<ul><li>Green marks text that you added, but that could not be saved.</li><li>Red marks text that you removed, but that could not be deleted.</li><li>Review each conflict, and then mark it as resolved to remove it from this list.</li></ul>"));
							
							for (int i = 0; i < size; i++) {
								ConflictView conflictView = new ConflictView(jsonConflicts.get(i).isObject());
								conflictView.setWidth("100%");
								conflictsList.add(conflictView);
							}
						} else {
							conflictsList.add(new Label("Your account has no sync conflicts."));
						}

						spaceLastWidgetInPanel(conflictsList);

						Documents.endProgressWithAlert(null);
					} else {
						Documents.endProgressWithAlert("Couldn't load conflicts (" + response.getStatusText() + ")");
					}
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load conflicts\n\n" + e);
		}
		return null;
	}
}

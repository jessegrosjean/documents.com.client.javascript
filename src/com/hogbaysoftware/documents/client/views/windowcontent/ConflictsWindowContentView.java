package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;

public class ConflictsWindowContentView extends WindowContentView {
	private VerticalPanel conflictsViewPanel = new VerticalPanel();
	private VerticalPanel conflictsList = new VerticalPanel();

	public ConflictsWindowContentView() {
		conflictsViewPanel.add(conflictsList);
		initWidget(conflictsViewPanel);
	}
	
	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().setWindowTitle("Conflicts");
	}

	public Request refresh() {
		Documents.beginProgress("Loading conflicts...");
		
		conflictsList.clear();
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, Documents.formatServiceURL("documents/conflicts"));

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load conflicts\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						JSONArray jsonConflicts = JSONParser.parse(response.getText()).isArray();
						int size = jsonConflicts.size();

						if (size > 0) {
							for (int i = 0; i < size; i++) {
								JSONObject jsonConflict = jsonConflicts.get(i).isObject();
								String documentID = jsonConflict.get("id").isString().stringValue();
								String documentName = jsonConflict.get("name").isString().stringValue();
								//String editVersion = jsonConflict.get("version").isString().stringValue();
								//String editDate = jsonConflict.get("created").isString().stringValue();
								//String conflicts = jsonConflict.get("conflicts").isString().stringValue();
								conflictsList.add(new Hyperlink(documentName, documentID));
							}
						} else {
							conflictsList.add(new Label("Your account has no conflicts."));
						}
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

package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.Document;

public class RevisionsContentView extends ContentView {
	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel revisionsList = new VerticalPanel();	
	
	public RevisionsContentView() {
		initWidget(scrollPanel);
		scrollPanel.add(revisionsList);
	}
		
	@Override
	public void viewDidShow() {
		super.viewDidShow();
		Document document = Documents.getSharedInstance().getDocument();
		Documents.getSharedInstance().getTitleView().setWindowTitlePath(document.getDisplayName(), document.getID(), "Revisions", document.getID() + "/revisions");
	}

	public Request refreshFromServer() {
		final Document document = Documents.getSharedInstance().getDocument();
		if (!document.existsOnServer()) {
			return null;
		}

		Documents.beginProgress("Loading revisions...");
		
		revisionsList.clear();
		revisionsList.add(new Label("Loading..."));

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/v1/documents/" + document.getID() + "/revisions/");

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load revisions\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					revisionsList.clear();
				
					revisionsList.add(new HTML("<p><strong>I just changed the way that document revisions are stored and created. This should fix a number of sync errors that were occurring.</strong></p>"));
					revisionsList.add(new HTML("<ul><li>I will keep an old <a href=\"http://1.latest.hogbaywriteroom.appspot.com/\">version of the site available</a> for 1 week. You can access your old revisions there during that time.</li><li>The new revision system is creating a new revision on every save, but in the future this is likely to change. For example a new revision might not be created if you only add (and don&#8217;t delete) text.</li><li>The new revision system is keeping all revisions, but this is also likely to change. Over time, to save disk space, old revisions may be pruned (deleted). Old revisions with few changes are the most likely to get pruned.</li></ul>"));
					revisionsList.add(new HTML("<p><strong>New Revisions:</strong></p>"));

					if (200 == response.getStatusCode()) {
						JSONArray jsonRevisions = JSONParser.parse(response.getText()).isArray();
						int size = jsonRevisions.size();

						for (int i = 0; i < size; i++) {							
							String revisionID = jsonRevisions.get(i).isString().stringValue();
							if (i == 0) {
								revisionsList.add(new Hyperlink(revisionID + " (latest)", document.getID() + "/revisions/" + revisionID));							
							} else {
								revisionsList.add(new Hyperlink(revisionID, document.getID() + "/revisions/" + revisionID));							
							}
						}

						spaceLastWidgetInPanel(revisionsList);

						Documents.endProgressWithAlert(null);
					} else {
						Documents.endProgressWithAlert("Couldn't load revisions (" + response.getStatusText() + ")");
					}
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load revisions\n\n" + e);
		}
		return null;
	}	
}

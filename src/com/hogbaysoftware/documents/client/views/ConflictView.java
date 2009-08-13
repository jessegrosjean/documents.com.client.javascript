package com.hogbaysoftware.documents.client.views;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.hogbaysoftware.documents.client.Documents;
import com.hogbaysoftware.documents.client.model.DiffMatchPatch;
import com.hogbaysoftware.documents.client.model.Patch;

public class ConflictView extends Composite implements ClickHandler {
	private VerticalPanel mainPanel = new VerticalPanel();
	private JSONObject conflict;
	
	public ConflictView(JSONObject aConflict) {
		initWidget(mainPanel);
		
		mainPanel.addStyleName("conflict-view");
		
		conflict = aConflict;
		String id = conflict.get("id").isString().stringValue();
		String document_id = conflict.get("document_id").isString().stringValue();
		String name = conflict.get("name").isString().stringValue();
		
		DiffMatchPatch dmp = new DiffMatchPatch();
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new HTML("<em>Document:</em> <a href=\"#" + document_id + "\">" + name + "</a>"));
		
		Widget markAsResolved = new MenuItemView("Mark As Resolved", this);
		markAsResolved.getElement().setAttribute("style", "margin:0;");
		panel.add(markAsResolved);
		panel.setCellHorizontalAlignment(markAsResolved, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.setWidth("100%");
		
		mainPanel.add(panel);
		mainPanel.setCellWidth(panel, "100%");
		
		mainPanel.add(new HTML("<em>Revision:</em> <a href=\"#" + document_id + "/revisions/" + id + "\">" + id + "</a>"));
		
		JsArray<Patch> failedPatches = dmp.patch_fromText(conflict.get("conflicts").isString().stringValue());
		for (int i = 0; i < failedPatches.length(); i++) {
			mainPanel.add(new HTML("<div class=\"conflict-diffs\">" + dmp.diff_prettyHtml(failedPatches.get(i).getDiffs()) + "</div>"));
		}
	}

	public void onClick(ClickEvent event) {
		Documents.beginProgress("Resolving conflict...");

		JSONObject jsonDocument = new JSONObject();
		jsonDocument.put("conflicts_resolved", JSONBoolean.getInstance(true));
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/v1/documents/" + conflict.get("document_id").isString().stringValue() + "/revisions/" + conflict.get("id").isString().stringValue());
		builder.setHeader("X-HTTP-Method-Override", "PUT");

		try {
			builder.sendRequest(jsonDocument.toString(), new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't resolve conflict\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Documents.endProgressWithAlert(null);
						History.fireCurrentHistoryState();
					} else {
						Documents.endProgressWithAlert("Couldn't resolve conflict (" + response.getStatusText() + ")");
					}
				}
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't resolve conflict\n\n" + e);
		}
	}
}

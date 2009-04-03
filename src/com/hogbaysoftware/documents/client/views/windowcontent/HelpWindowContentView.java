package com.hogbaysoftware.documents.client.views.windowcontent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HTML;
import com.hogbaysoftware.documents.client.Documents;

public class HelpWindowContentView extends WindowContentView {
	private HTML helpHTML = new HTML();
	
	public HelpWindowContentView() {
		initWidget(helpHTML);
	}

	public void viewDidShow() {
		super.viewDidShow();
		Documents.getSharedInstance().getWindowView().setWindowTitlePath("Help", "help");
	}

	public Request refreshFromServer() {
		Documents.beginProgress("Loading help...");
		
		String url = GWT.getModuleBaseURL() + "help.html";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			return builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					Documents.endProgressWithAlert("Couldn't load help\n\n" + e);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						helpHTML.setHTML(response.getText());
						Documents.endProgressWithAlert(null);
					} else {
						Documents.endProgressWithAlert("Couldn't load help (" + response.getStatusText() + ")");
					}
				}       
			});
		} catch (RequestException e) {
			Documents.endProgressWithAlert("Couldn't load help\n\n" + e);
		}
		return null;
	}
}

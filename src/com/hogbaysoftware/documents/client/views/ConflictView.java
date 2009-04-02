package com.hogbaysoftware.documents.client.views;

import java.util.Date;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hogbaysoftware.documents.client.model.DiffMatchPatch;
import com.hogbaysoftware.documents.client.model.Patch;

public class ConflictView extends Composite {
	private VerticalPanel mainPanel = new VerticalPanel();
	private JSONObject conflict;
	
	public ConflictView(JSONObject aConflict) {
		initWidget(mainPanel);
		
		addStyleName("conflict");
		
		conflict = aConflict;
		
		DiffMatchPatch dmp = new DiffMatchPatch();
		
		mainPanel.add(new HTML("Document " + "<strong>\"" + conflict.get("name").isString().stringValue() + "\"</strong>" + " (" + (int) conflict.get("version").isNumber().doubleValue() + ") on " + conflict.get("created").isString().stringValue().split(" ")[0]));
				
		//mainPanel.add(new HTML("<strong>Date:</strong> " + conflict.get("created").isString().stringValue().split(" ")[0]));
		
		
		JsArray<Patch> failedPatches = dmp.patch_fromText(conflict.get("conflicts").isString().stringValue());
		for (int i = 0; i < failedPatches.length(); i++) {
			mainPanel.add(new HTML("<div>" + dmp.diff_prettyHtml(failedPatches.get(i).getDiffs()) + "</div>"));
		}
		
/*		
		int length = failedPatches.length();
		String failedPatchesAsText = dmp.patch_toText(failedPatches);
		
		String htmlPatches = dmp.diff_prettyHtml(failedPatches);
		mainPanel.add(new HTML(htmlPatches));
	*/	
		//mainPanel.add(new Label(jsonConflict.get("name").isString().stringValue()));
		//String documentID = jsonConflict.get("id").isString().stringValue();
		//String documentName = jsonConflict.get("name").isString().stringValue();
		//String editVersion = jsonConflict.get("version").isString().stringValue();
		//String editDate = jsonConflict.get("created").isString().stringValue();
		//String conflicts = jsonConflict.get("conflicts").isString().stringValue();
	}
	
	
}

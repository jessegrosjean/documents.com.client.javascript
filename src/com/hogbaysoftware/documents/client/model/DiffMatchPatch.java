package com.hogbaysoftware.documents.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class DiffMatchPatch { 	
	private JavaScriptObject dmp;
	
	public DiffMatchPatch() {
		dmp = initDMP();
	}

	public JavaScriptObject patch_fromText(String textline) {
		return patch_fromTextImpl(dmp, textline);
	}
	
	private native static JavaScriptObject initDMP() /*-{
		return new $wnd.diff_match_patch();
	}-*/;
		
	private native static JavaScriptObject patch_fromTextImpl(JavaScriptObject dmp, String textline) /*-{
		return dmp.patch_fromText(textline);
	}-*/;
	
}

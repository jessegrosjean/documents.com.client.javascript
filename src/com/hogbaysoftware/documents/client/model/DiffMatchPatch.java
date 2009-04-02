package com.hogbaysoftware.documents.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DiffMatchPatch { 	
	private JavaScriptObject dmp;
	
	public DiffMatchPatch() {
		dmp = initDMP();
	}

	private native static JavaScriptObject initDMP() /*-{
		return new $wnd.diff_match_patch();
	}-*/;

	public String diff_prettyHtml(JsArray<Diff> diffs) {
		return diff_prettyHtmlImpl(dmp, diffs);
	}

	private native static String diff_prettyHtmlImpl(JavaScriptObject dmp, JsArray<Diff> diffs) /*-{
		return dmp.diff_prettyHtml(diffs);
	}-*/;

	public JsArray<Patch> patch_make(String text1, String text2) {
		return patch_makeImpl(dmp, text1, text2);
	}

	private native static JsArray<Patch> patch_makeImpl(JavaScriptObject dmp, String text1, String text2) /*-{
		return dmp.patch_make(text1, text2);
	}-*/;

	public String patch_toText(JsArray<Patch> patches) {
		return patch_toTextImpl(dmp, patches);
	}

	private native static String patch_toTextImpl(JavaScriptObject dmp, JsArray<Patch> patches) /*-{
		return dmp.patch_toText(patches);
	}-*/;

	
	public JsArray<Patch> patch_fromText(String textline) {
		return patch_fromTextImpl(dmp, textline);
	}
			
	private native static JsArray<Patch> patch_fromTextImpl(JavaScriptObject dmp, String textline) /*-{
		return dmp.patch_fromText(textline);
	}-*/;
	
}

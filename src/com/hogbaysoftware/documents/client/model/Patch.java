package com.hogbaysoftware.documents.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Patch extends JavaScriptObject {
	protected Patch() {}
	
	public final native JsArray<Diff> getDiffs() /*-{ return this.diffs; }-*/;
}

package com.hogbaysoftware.documents.client.views;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.DialogBox;

public class DialogView extends DialogBox {

	public DialogView() {
		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				updateLayout();
			}
		});
	}
	
	public void centerTop() {
		super.center();
		setPopupPosition(getPopupLeft(), getPopupTop() / 2);
	}
	
	public void updateLayout() {
		if (isAttached()) {
			centerTop();
		}
	}
}

package com.cwenhui.chowhound.bean;

import android.content.Context;

public class LoginActionItem {
	private String mTitle;
	private int mActionId = -1;
	
	public LoginActionItem(Context context, String title, int actionId) {
		mTitle = title;
		mActionId = actionId;
	}
	

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public int getActionId() {
		return mActionId;
	}

	public void setActionId(int actionId) {
		this.mActionId = actionId;
	}

}

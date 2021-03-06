/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.webcontentdisplay;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.webcontentdisplay.interactor.WebContentDisplayInteractor;
import com.liferay.mobile.screens.webcontentdisplay.interactor.WebContentDisplayInteractorImpl;
import com.liferay.mobile.screens.webcontentdisplay.view.WebContentDisplayViewModel;

import java.util.Locale;

/**
 * @author Jose Manuel Navarro
 */
public class WebContentDisplayScreenlet
	extends BaseScreenlet<WebContentDisplayViewModel, WebContentDisplayInteractor>
	implements WebContentDisplayListener {

	public WebContentDisplayScreenlet(Context context) {
		super(context);
	}

	public WebContentDisplayScreenlet(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public WebContentDisplayScreenlet(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}

	public void load() {
		performUserAction();
	}

	@Override
	public void onWebContentFailure(WebContentDisplayScreenlet source, Exception e) {
		getViewModel().showFailedOperation(null, e);

		if (_listener != null) {
			_listener.onWebContentFailure(this, e);
		}
	}

	@Override
	public String onWebContentReceived(WebContentDisplayScreenlet source, String html) {
		String modifiedHtml = html;

		if (_listener != null) {
			String listenerHtml = _listener.onWebContentReceived(this, html);

			if (listenerHtml != null) {
				modifiedHtml = listenerHtml;
			}
		}

		getViewModel().showFinishOperation(modifiedHtml);

		return modifiedHtml;
	}

	@Override
	public void loadingFromCache(boolean success) {
		if (_listener != null) {
			_listener.loadingFromCache(success);
		}
	}

	@Override
	public void retrievingOnline(boolean triedInCache, Exception e) {
		if (_listener != null) {
			_listener.retrievingOnline(triedInCache, e);
		}
	}

	@Override
	public void storingToCache(Object object) {
		if (_listener != null) {
			_listener.storingToCache(object);
		}
	}

	public void setListener(WebContentDisplayListener listener) {
		_listener = listener;
	}

	public boolean isJavascriptEnabled() {
		return _javascriptEnabled;
	}

	public void setJavascriptEnabled(boolean javascriptEnabled) {
		_javascriptEnabled = javascriptEnabled;
	}

	public String getArticleId() {
		return _articleId;
	}

	public void setArticleId(String articleId) {
		_articleId = articleId;
	}

	public Long getTemplateId() {
		return _templateId;
	}

	public void setTemplateId(Long templateId) {
		_templateId = templateId;
	}

	public OfflinePolicy getOfflinePolicy() {
		return _offlinePolicy;
	}

	public void setOfflinePolicy(OfflinePolicy offlinePolicy) {
		_offlinePolicy = offlinePolicy;
	}

	public boolean isAutoLoad() {
		return _autoLoad;
	}

	public void setAutoLoad(boolean autoLoad) {
		_autoLoad = autoLoad;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}


	protected void autoLoad() {
		if ((_articleId != null) && SessionContext.hasSession()) {
			try {
				load();
			}
			catch (Exception e) {
				onWebContentFailure(this, e);
			}
		}
	}

	@Override
	protected View createScreenletView(
		Context context, AttributeSet attributes) {

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attributes, R.styleable.WebContentDisplayScreenlet, 0, 0);

		_autoLoad = typedArray.getBoolean(R.styleable.WebContentDisplayScreenlet_autoLoad, true);

		_articleId = typedArray.getString(R.styleable.WebContentDisplayScreenlet_articleId);

		_groupId = castToLongOrUseDefault(typedArray.getString(
			R.styleable.WebContentDisplayScreenlet_groupId), LiferayServerContext.getGroupId());

		_templateId = castToLong(typedArray.getString(R.styleable.WebContentDisplayScreenlet_templateId));

		_javascriptEnabled = typedArray.getBoolean(
			R.styleable.WebContentDisplayScreenlet_javascriptEnabled, false);

		int offlinePolicy = typedArray.getInt(R.styleable.WebContentDisplayScreenlet_offlinePolicy,
			OfflinePolicy.REMOTE_ONLY.ordinal());
		_offlinePolicy = OfflinePolicy.values()[offlinePolicy];

		int layoutId = typedArray.getResourceId(
			R.styleable.WebContentDisplayScreenlet_layoutId, getDefaultLayoutId());

		typedArray.recycle();

		return LayoutInflater.from(context).inflate(layoutId, null);
	}

	@Override
	protected WebContentDisplayInteractor createInteractor(String actionName) {
		return new WebContentDisplayInteractorImpl(getScreenletId(), _offlinePolicy);
	}

	@Override
	protected void onUserAction(
		String userActionName, WebContentDisplayInteractor interactor, Object... args) {

		Locale locale = getResources().getConfiguration().locale;
		getViewModel().showStartOperation(userActionName);

		try {
			getInteractor().load(_groupId, _articleId, _templateId, locale);
		}
		catch (Exception e) {
			onWebContentFailure(this, e);
		}
	}

	@Override
	protected void onScreenletAttached() {
		if (_autoLoad) {
			autoLoad();
		}
	}

	private OfflinePolicy _offlinePolicy;
	private Long _templateId;
	private String _articleId;
	private boolean _autoLoad;
	private long _groupId;
	private boolean _javascriptEnabled;
	private WebContentDisplayListener _listener;

}
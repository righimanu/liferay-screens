/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.ddl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.liferay.mobile.screens.util.LiferayLogger;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jose Manuel Navarro
 */
public class NumberField extends Field<Number> {

	public static final Parcelable.Creator<NumberField> CREATOR =
		new Parcelable.Creator<NumberField>() {

			public NumberField createFromParcel(Parcel in) {
				return new NumberField(in);
			}

			public NumberField[] newArray(int size) {
				return new NumberField[size];
			}
		};

	public NumberField(Map<String, Object> attributes, Locale locale, Locale defaultLocale) {
		super(attributes, locale, defaultLocale);

		init(locale, defaultLocale);
	}

	protected NumberField(Parcel in) {
		super(in);

		init(getCurrentLocale(), getDefaultLocale());
	}

	@Override
	protected Number convertFromString(String stringValue) {
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		}

		try {
			return _labelFormatter.parse(stringValue);
		}
		catch (ParseException e) {
			try {
				return _defaultLabelFormatter.parse(stringValue);
			}
			catch (ParseException e1) {
				LiferayLogger.e("Error parsing decimal field", e);

			}
		}

		return null;
	}

	@Override
	protected String convertToData(Number value) {
		return (value == null) ? null : value.toString();
	}

	@Override
	protected String convertToFormattedString(Number value) {
		return (value == null) ? "" : _labelFormatter.format(value);
	}

	private void init(Locale locale, Locale defaultLocale) {
		_labelFormatter = NumberFormat.getNumberInstance(locale);
		_defaultLabelFormatter = NumberFormat.getNumberInstance(defaultLocale);
	}

	private NumberFormat _labelFormatter;
	private NumberFormat _defaultLabelFormatter;

}
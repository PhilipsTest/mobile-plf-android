package com.philips.cl.di.dev.pa.util;

import java.util.HashMap;
import java.util.Locale;

public class LanguageUtils {
	
	public static final String DEFAULT_LANGUAGE = "EN";
	public static final String LANGUAGE_ZH = "ZH-HANT";
	
	private static HashMap<Locale, String> customMapping = null;
	static {
		customMapping = new HashMap<Locale, String>();
		customMapping.put(Locale.TRADITIONAL_CHINESE, "ZH-HANT");
		customMapping.put(Locale.SIMPLIFIED_CHINESE, "ZH-HANS");
	}
	
	public static String getLanguageForLocale(Locale loc) {
		if (loc == null) return DEFAULT_LANGUAGE;

		String language = loc.getLanguage();

		if (language == null || language.isEmpty())
			return DEFAULT_LANGUAGE;
		
		/*
		 * In Some chinies phone we are getting Local Language as "ZH". Like : Lenovo A298t
		 * So we are treating it as "ZH-HANT".
		 */
		if(language.toUpperCase().equalsIgnoreCase("ZH")) return LANGUAGE_ZH;
		
		for (Locale custom : customMapping.keySet()) {
			if (!custom.equals(loc)) continue;
			language = customMapping.get(custom);
		}
		
		return language.toUpperCase();
	}
	

}

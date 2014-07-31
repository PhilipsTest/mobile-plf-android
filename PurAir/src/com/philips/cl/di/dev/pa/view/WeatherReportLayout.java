package com.philips.cl.di.dev.pa.view;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.datamodel.Weatherdto;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Utils;

public class WeatherReportLayout extends  LinearLayout {
	
	String[] nextFourDays;
	String[] hrsDays;
	
	public WeatherReportLayout(Context context) {
		super(context);
	}
	
	public WeatherReportLayout(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	public WeatherReportLayout(Context context, AttributeSet attr, int defStyle) {
		super(context, attr);
	}

	/**
	 * Constructor
	 * @param Context
	 * @param AttributeSet
	 * @param int number of view to show
	 * */
	public WeatherReportLayout(final Context context, AttributeSet attrs, String timeStr, 
			List<Weatherdto> weatherDetails) {
		super(context, attrs);
		
		if (weatherDetails == null) {
			return;
		}
		nextFourDays = new String[4];
		hrsDays = new String[weatherDetails.size()];
		weatherForecatDays(context, timeStr);
		
		/**
		 * For weather report condition
		 * */
		if (weatherDetails.size() == 4) {
			fourDaysWeatherForecast(context, weatherDetails);
		} else {
			todaysWeather(context, weatherDetails);
		}
		
	}
	
	private void todaysWeather(Context context, List<Weatherdto> weatherDetails) {
		/** Today's weather report*/
		
		for (int i = 0; i < weatherDetails.size(); i++) {
			float tempInCentigrade = weatherDetails.get(i).getTempInCentigrade();
			weatherDetails.get(i).getTempInFahrenheit();
			weatherDetails.get(i).getDate();
			String time = weatherDetails.get(i).getTime().substring(10,16);
			String weatherDesc = weatherDetails.get(i).getWeatherDesc();
			String isdaytime = weatherDetails.get(i).getIsdaytime();
			
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.od_today_weather_layout, null);
			v.setPadding(10, 10, 10, 10);
			
			FontTextView timeTxt = (FontTextView) v.findViewById(R.id.odTodyWeatherTime);
			FontTextView tempTxt = (FontTextView) v.findViewById(R.id.odTodyWeatherTemp);
			ImageView weatherImg = (ImageView) v.findViewById(R.id.odTodyWeatherImg);
			
			timeTxt.setText(time);
			tempTxt.setText(tempInCentigrade+AppConstants.UNICODE_DEGREE);
			weatherImg.setImageDrawable(
					Utils.getOutdoorTemperatureImage(context,weatherDesc, isdaytime));
			LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			this.addView(v, parentParams);
		}
	}
	
	private void fourDaysWeatherForecast(Context context, List<Weatherdto> weatherDetails) {
		/** Next 4 days weather report*/
		int count = 9;
		for (int i = 0; i < 5; i++) {
			float windSpeedTemp = 0;
			String weatherDesc = null;
			String windDirection = null;
			float windDegree = 0;
			float maxTempC = 0;
			float minTempC = 0;
			float windSpeed = 0;
			for (int j = 0; j < 8; j++) {
				weatherDetails.get(count).getTempInCentigrade();
				weatherDetails.get(count).getTempInFahrenheit();
				weatherDetails.get(count).getDate();
				weatherDetails.get(count).getTime();
				maxTempC = weatherDetails.get(count).getMaxTempC();
				weatherDetails.get(count).getMaxTempF();
				minTempC = weatherDetails.get(count).getMinTempC();
				windSpeed = weatherDetails.get(count).getWindSpeed();
				weatherDetails.get(count).getMinTempF();
				
				if (j == 4) {
					weatherDesc = weatherDetails.get(count).getWeatherDesc();
					weatherDetails.get(count).getIsdaytime();
					windDirection = weatherDetails.get(count).getWindDirection();
					windDegree = weatherDetails.get(count).getWindDegree();
				}
				
				if (windSpeed > windSpeedTemp) {
					windSpeedTemp = windSpeed ;
				} 
				
				count++;
			}
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.od_last_five_weather_forcast, null);
			v.setPadding(10, 10, 10, 10);
			
			FontTextView dayTxt = (FontTextView) v.findViewById(R.id.odDayWeatherForcast);
			FontTextView maxTempTxt = (FontTextView) v.findViewById(R.id.odWeatherForcastMaxTemp);
			FontTextView minTempTxt = (FontTextView) v.findViewById(R.id.odWeatherForcastMinTemp);
			FontTextView windSpeedTxt = (FontTextView) v.findViewById(R.id.odWeatherForcastWind);
			ImageView weatherImg = (ImageView) v.findViewById(R.id.odWeatherForcastImg);
			ImageView windDirImg = (ImageView) v.findViewById(R.id.odWeatherForcastWindImg);
			
			dayTxt.setText(nextFourDays[i]);
			weatherImg.setImageDrawable(
					Utils.getOutdoorTemperatureImage(context, weatherDesc, "yes"));
			Utils.setOutdoorWeatherDirImg(context, windSpeedTemp, windDirection, windDegree, windDirImg);
			maxTempTxt.setText(maxTempC+"\u2103");
			minTempTxt.setText(minTempC+"\u2103");
			windSpeedTxt.setText(String.format("%.1f", windSpeedTemp)+" km/h");
			
			LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			this.addView(v, parentParams);
		}
	}
	
	private void weatherForecatDays(Context context, String timeStr) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		if (timeStr != null && timeStr.length() > 0) {
			try {
				hourOfDay = Integer.parseInt(timeStr.substring(11, 13));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		int dayInt = cal.get(Calendar.DAY_OF_WEEK);
		for (int j = 0; j < nextFourDays.length; j++) {
			if (dayInt < 7) {
				dayInt++;
			} else {
				dayInt = 1;
			}
			String dayStr = Utils.getDayOfWeek(context, dayInt);
			nextFourDays[j] = dayStr;
		}
		
		for (int j = 0; j < hrsDays.length; j++) {
			String tempHr = null;
			if (hourOfDay < 10) {
				tempHr = context.getString(R.string.one_zero) + hourOfDay + context.getString(R.string.colon_2_zero);
			} else {
				tempHr = hourOfDay + context.getString(R.string.colon_2_zero);
			}
			hrsDays[j] = tempHr;
			hourOfDay = hourOfDay + 3;
			if (hourOfDay > 23) {
				hourOfDay = hourOfDay - 24;
			} 
		}
	}
}
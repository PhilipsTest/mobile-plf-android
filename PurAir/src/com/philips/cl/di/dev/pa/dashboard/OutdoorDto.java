package com.philips.cl.di.dev.pa.dashboard;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.R;


public class OutdoorDto {

	//TODO : Fix aqiTitle, aqiSummary, weatherIcon
	
	private String updatedTime;
	private String cityName;
	private String temperature;
	private String weatherIcon;
	private String aqi;
	
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	public String getAqi() {
		return aqi;
	}
	public void setAqi(String aqi) {
		if(aqi == null) {
			aqi = "null";
		}
		this.aqi = aqi;
	}
	
	public String getAqiTitle() {
		if(aqi == null || aqi.isEmpty()) {
			return "";
		}
		
		try {
			int aqiInt = Integer.parseInt(aqi);
			if(aqiInt >= 0 && aqiInt <= 50) {
				 return PurAirApplication.getAppContext().getString(R.string.good);
			} else if(aqiInt > 50 && aqiInt <= 100) {
				return PurAirApplication.getAppContext().getString(R.string.moderate);
			} else if(aqiInt > 100 && aqiInt <= 150) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 150 && aqiInt <= 200) {
				return PurAirApplication.getAppContext().getString(R.string.unhealthy);
			} else if(aqiInt > 200 && aqiInt <= 300) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 300 && aqiInt <= 500) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			}
		} catch (NumberFormatException e) {
			return "";
		}
		return "";
	}

	public String getAqiSummary() {
		if(aqi == null || aqi.isEmpty()) {
			return "";
		}
		
		try {
			int aqiInt = Integer.parseInt(aqi);
			if(aqiInt >= 0 && aqiInt <= 50) {
				 return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 50 && aqiInt <= 100) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 100 && aqiInt <= 150) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 150 && aqiInt <= 200) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 200 && aqiInt <= 300) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			} else if(aqiInt > 300 && aqiInt <= 500) {
				return PurAirApplication.getAppContext().getString(R.string.todo);
			}
		} catch (NumberFormatException e) {
			return "";
		}
		return "";
	}
	
	public int getAqiPointerImageResId() {
//		if(aqi == null || aqi.isEmpty()) {
//			return R.drawable.blue_circle_with_arrow_small;
//		}
//		
//		try {
//			int aqiInt = Integer.parseInt(aqi);
//			if(aqiInt >= 0 && aqiInt <= 50) {
//				 return R.drawable.blue_circle_with_arrow_small;
//			} else if(aqiInt > 50 && aqiInt <= 100) {
//				return R.drawable.pink_circle_with_arrow_small;
//			} else if(aqiInt > 100 && aqiInt <= 150) {
//				return R.drawable.light_pink_circle_arrow_small;
//			} else if(aqiInt > 150 && aqiInt <= 200) {
//				return R.drawable.light_pink_circle_arrow1_small;
//			} else if(aqiInt > 200 && aqiInt <= 300) {
//				return R.drawable.red_circle_arrow_small;
//			} else if(aqiInt > 300 && aqiInt <= 500) {
//				return R.drawable.light_red_circle_arrow_small;
//			}
//		} catch (NumberFormatException e) {
//			return R.drawable.blue_circle_with_arrow_small;
//		}
		return R.drawable.blue_circle_with_arrow_small;
	}
	
	public float getAqiPointerRotaion() {
		if(aqi == null || aqi.isEmpty()) {
			return 0.0f;
		}
		float rotation = 0.0f;
		try {
			int aqiInt = Integer.parseInt(aqi);
			if(aqiInt >= 0 && aqiInt <= 50) {
				rotation = aqiInt * 0.54f;
			} else if(aqiInt > 50 && aqiInt <= 100) {
				aqiInt -= 50;
				rotation = 28 + aqiInt * 0.54f;
			} else if(aqiInt > 100 && aqiInt <= 150) {
				aqiInt -= 100;
				rotation = 57 + aqiInt * 0.54f;
			} else if(aqiInt > 150 && aqiInt <= 200) {
				aqiInt -= 150;
				rotation = 86 + aqiInt * 0.54f;
			} else if(aqiInt > 200 && aqiInt <= 300) {
				aqiInt -= 200;
				rotation = 114 + aqiInt * 0.705f;
			} else if(aqiInt > 300) {
				aqiInt -= 300;
				rotation = 187 + aqiInt * 0.163f;
				if(rotation > 301) {
					rotation = 301;
				}
			}
		} catch (NumberFormatException e) {
			return 0.0f;
		}
		return rotation;
	}

	public String getWeatherIcon() {
		return weatherIcon;
	}
	public void setWeatherIcon(String weatherIcon) {
		this.weatherIcon = weatherIcon;
	}
}

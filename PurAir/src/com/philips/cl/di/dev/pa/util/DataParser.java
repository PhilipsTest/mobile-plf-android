package com.philips.cl.di.dev.pa.util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.philips.cl.di.dev.pa.constant.ParserConstants;
import com.philips.cl.di.dev.pa.datamodel.AirPortInfo;
import com.philips.cl.di.dev.pa.datamodel.City;
import com.philips.cl.di.dev.pa.datamodel.CityDetails;
import com.philips.cl.di.dev.pa.datamodel.DeviceDto;
import com.philips.cl.di.dev.pa.datamodel.DeviceWifiDto;
import com.philips.cl.di.dev.pa.datamodel.IndoorHistoryDto;
import com.philips.cl.di.dev.pa.datamodel.OutdoorAQIEventDto;
import com.philips.cl.di.dev.pa.datamodel.Weatherdto;
import com.philips.cl.di.dev.pa.firmware.FirmwarePortInfo;
import com.philips.cl.di.dev.pa.scheduler.SchedulePortInfo;

/***
 * This class it used to parse data for AirPurifier event
 * @author 310124914
 *
 */
public class DataParser {

	private DataParser() {
	}

	public static AirPortInfo parseAirPurifierEventData(String dataToParse) {
		AirPortInfo airPurifierEvent = null ;
		try {			
			if( dataToParse != null ) {
				JSONObject jsonObj = new JSONObject(dataToParse) ;
				airPurifierEvent = new AirPortInfo() ;			

				airPurifierEvent.setMachineMode(jsonObj.getString(ParserConstants.MACHINE_MODE)) ;
				airPurifierEvent.setFanSpeed(jsonObj.getString(ParserConstants.FAN_SPEED)) ;
				airPurifierEvent.setPowerMode(jsonObj.getString(ParserConstants.POWER_MODE)) ;
				String aqi = jsonObj.getString(ParserConstants.AQI) ;
				if(aqi != null && !aqi.equals(""))
					airPurifierEvent.setIndoorAQI(Integer.parseInt(aqi)) ;

				airPurifierEvent.setAqiL(Integer.parseInt(jsonObj.getString(ParserConstants.AQI_LIGHT))) ;
				airPurifierEvent.setAqiThreshold(Integer.parseInt(jsonObj.getString(ParserConstants.AQI_THRESHOLD))) ;
				airPurifierEvent.setDtrs(Integer.parseInt(jsonObj.getString(ParserConstants.DTRS))) ;
				airPurifierEvent.setFilterStatus1(Integer.parseInt(jsonObj.getString(ParserConstants.FILTER_STATUS_1))) ;
				airPurifierEvent.setFilterStatus2(Integer.parseInt(jsonObj.getString(ParserConstants.FILTER_STATUS_2))) ;
				airPurifierEvent.setFilterStatus3(Integer.parseInt(jsonObj.getString(ParserConstants.FILTER_STATUS_3))) ;
				airPurifierEvent.setFilterStatus4(Integer.parseInt(jsonObj.getString(ParserConstants.FILTER_STATUS_4))) ;
				airPurifierEvent.setReplaceFilter1(jsonObj.getString(ParserConstants.CLEAN_FILTER_1)) ;
				airPurifierEvent.setReplaceFilter2(jsonObj.getString(ParserConstants.REP_FILTER_2)) ;
				airPurifierEvent.setReplaceFilter3(jsonObj.getString(ParserConstants.REP_FILTER_3)) ;
				airPurifierEvent.setReplaceFilter4(jsonObj.getString(ParserConstants.REP_FILTER_4)) ;
				airPurifierEvent.setChildLock(Integer.parseInt(jsonObj.getString(ParserConstants.CHILD_LOCK))) ;
				airPurifierEvent.setpSensor(Integer.parseInt(jsonObj.getString(ParserConstants.PSENS))) ;
				airPurifierEvent.settFav(Integer.parseInt(jsonObj.getString(ParserConstants.TFAV))) ;	
				airPurifierEvent.setActualFanSpeed(jsonObj.getString(ParserConstants.ACTUAL_FAN_SPEED));
			}
		} catch (JSONException e) {
			ALog.e(ALog.PARSER, "JSONException") ;
			return null;
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
			return null;
		} catch (JsonSyntaxException e2) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
			return null;
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}

		return airPurifierEvent ;
	}

	public static FirmwarePortInfo parseFirmwareEventData(String dataToParse) {
		Gson gson = new GsonBuilder().create();

		try {
			FirmwarePortInfo firmwareEventDto = gson.fromJson(dataToParse, FirmwarePortInfo.class);
			if (firmwareEventDto.isValid()) {
				return firmwareEventDto;
			}
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
			return null;
		} catch (JsonSyntaxException e2) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
			return null;
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}
		return null;
	}

	public  static List<IndoorHistoryDto> parseHistoryData(String dataToParse) {
		//Log.i("PARSE", "Parse History Data\n"+dataToParse) ;
		List<IndoorHistoryDto> indoorHistoryList = null ;
		IndoorHistoryDto indoorAQIHistoryDto = null ;
		JSONObject jsonObject = null ;
		try {
			jsonObject = new JSONObject(dataToParse);
			JSONArray seriesJson = jsonObject.getJSONArray(ParserConstants.SERIES) ;

			if( seriesJson != null && seriesJson.length() > 0 ) {
				Log.i("PARSE", "Series: "+seriesJson.length()) ;
				indoorHistoryList = new ArrayList<IndoorHistoryDto>() ;
				int seriesArrayLength = seriesJson.length() ;
				for ( int index = 0 ; index < seriesArrayLength ; index ++ ) {
					JSONObject indoorJsonObj = seriesJson.getJSONObject(index) ;
					indoorAQIHistoryDto = new IndoorHistoryDto() ;
					indoorAQIHistoryDto.setTimeStamp(indoorJsonObj.getString(ParserConstants.TIMESTAMP_INDOORAQI_HISTORY)) ;

					JSONObject dataKeyValuePairJson = indoorJsonObj.getJSONObject(ParserConstants.DATA_VALUE_KEYPAIRS) ;
					if ( dataKeyValuePairJson != null ) {
						indoorAQIHistoryDto.setAqi(Float.parseFloat(dataKeyValuePairJson.getString(ParserConstants.AQI))) ;
						indoorAQIHistoryDto.setTfav(Integer.parseInt(dataKeyValuePairJson.getString(ParserConstants.TFAV))) ;
					}
					indoorHistoryList.add(indoorAQIHistoryDto) ;
				}
			}
		} catch (JSONException e) {
			ALog.e(ALog.PARSER, "JSONException") ;
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
		} catch (JsonSyntaxException e2) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}
		if (indoorHistoryList != null && indoorHistoryList.size() > 0 ) {
			Log.i("PARSE", "Size: "+indoorHistoryList.size()) ;
		}
		return indoorHistoryList ;
	}

	public static AirPortInfo parseAirPurifierEventDataFromCPP(String dataToParse)  {
		AirPortInfo airPurifierEvent = null ;
		JSONObject jsonObject = null ;
		try {
			jsonObject = new JSONObject(dataToParse);
			JSONObject airPuriferJson = jsonObject.getJSONObject("data") ;
			airPurifierEvent = new AirPortInfo() ;	

			airPurifierEvent.setMachineMode(airPuriferJson.getString(ParserConstants.MACHINE_MODE)) ;
			airPurifierEvent.setFanSpeed(airPuriferJson.getString(ParserConstants.FAN_SPEED)) ;
			airPurifierEvent.setPowerMode(airPuriferJson.getString(ParserConstants.POWER_MODE)) ;
			String aqi = airPuriferJson.getString(ParserConstants.AQI) ;
			if(aqi != null && !aqi.equals(""))
				airPurifierEvent.setIndoorAQI(Integer.parseInt(aqi)) ;

			airPurifierEvent.setAqiL(Integer.parseInt(airPuriferJson.getString(ParserConstants.AQI_LIGHT))) ;
			airPurifierEvent.setAqiThreshold(Integer.parseInt(airPuriferJson.getString(ParserConstants.AQI_THRESHOLD))) ;
			airPurifierEvent.setDtrs(Integer.parseInt(airPuriferJson.getString(ParserConstants.DTRS))) ;
			airPurifierEvent.setFilterStatus1(Integer.parseInt(airPuriferJson.getString(ParserConstants.FILTER_STATUS_1))) ;
			airPurifierEvent.setFilterStatus2(Integer.parseInt(airPuriferJson.getString(ParserConstants.FILTER_STATUS_2))) ;
			airPurifierEvent.setFilterStatus3(Integer.parseInt(airPuriferJson.getString(ParserConstants.FILTER_STATUS_3))) ;
			airPurifierEvent.setFilterStatus4(Integer.parseInt(airPuriferJson.getString(ParserConstants.FILTER_STATUS_4))) ;
			airPurifierEvent.setReplaceFilter1(airPuriferJson.getString(ParserConstants.CLEAN_FILTER_1)) ;
			airPurifierEvent.setReplaceFilter2(airPuriferJson.getString(ParserConstants.REP_FILTER_2)) ;
			airPurifierEvent.setReplaceFilter3(airPuriferJson.getString(ParserConstants.REP_FILTER_3)) ;
			airPurifierEvent.setReplaceFilter4(airPuriferJson.getString(ParserConstants.REP_FILTER_4)) ;
			airPurifierEvent.setChildLock(Integer.parseInt(airPuriferJson.getString(ParserConstants.CHILD_LOCK))) ;
			airPurifierEvent.setpSensor(Integer.parseInt(airPuriferJson.getString(ParserConstants.PSENS))) ;
			airPurifierEvent.settFav(Integer.parseInt(airPuriferJson.getString(ParserConstants.TFAV))) ;
			airPurifierEvent.setActualFanSpeed(airPuriferJson.getString(ParserConstants.ACTUAL_FAN_SPEED));

		} catch (JSONException e) {
			ALog.e("Exception", "JSONException -- "+ e.getMessage()) ;
			return null ;
		}catch(NumberFormatException nfe ) {
			ALog.e("Exception", "Number Format exception -- "+ nfe.getMessage()) ;
			return null ;
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
			return null ;
		} catch (JsonSyntaxException e2) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
			return null ;
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}
		return airPurifierEvent ;
	}


	public static OutdoorAQIEventDto parseOutdoorAQIData(String dataToParse) {
		ALog.i(ALog.OUTDOOR_DETAILS, "parseOutdoorAQIData parsing");
		if (dataToParse == null) {
			return null;
		}
		try {
			Gson gson = new GsonBuilder().create() ;
			OutdoorAQIEventDto outdoorAQI = gson.fromJson(dataToParse, OutdoorAQIEventDto.class) ;
			return outdoorAQI;
		} catch (JsonSyntaxException e) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
			return null;
		} catch (JsonIOException ioe) {
			ALog.e(ALog.PARSER, "JsonIOException");
			return null;
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}
	}

	public static List<Weatherdto> parseWeatherData(String dataToParse) {
		if (dataToParse == null) {
			return null;
		}
		List<Weatherdto> weatherForecastList = null ;
		Weatherdto weatherDto = new Weatherdto()  ;
		String date = "";

		try {
			JSONObject jsonObj = new JSONObject(dataToParse) ;

			JSONObject dataObj = jsonObj.getJSONObject(ParserConstants.DATA) ;

			JSONArray currentCondition = dataObj.getJSONArray(ParserConstants.CURRENT_CONDITION) ;

			weatherForecastList = new ArrayList<Weatherdto>() ;

			JSONObject currentConditionObj = currentCondition.getJSONObject(0) ;

			weatherDto.setTempInCentigrade(Float.parseFloat(currentConditionObj.getString("temp_C"))) ;
			weatherDto.setTempInFahrenheit(Float.parseFloat(currentConditionObj.getString("temp_F"))) ;
			weatherDto.setTime(currentConditionObj.getString(ParserConstants.OBSERVATION_TIME)) ;
			weatherDto.setWeatherDesc(currentConditionObj.getJSONArray(ParserConstants.WEATHER_DESC).optJSONObject(0).getString(ParserConstants.VALUE));
			weatherDto.setIsdaytime(currentConditionObj.getString(ParserConstants.IS_DAY_TIME));
			weatherForecastList.add(weatherDto) ;

			JSONArray weatherArray = dataObj.getJSONArray(ParserConstants.WEATHER) ;

			if ( weatherArray != null && weatherArray.length() > 0 ) {				
				int length = weatherArray.length() ;

				for( int index = 0 ; index < length ; index ++ ) {
					JSONObject weatherJSON = weatherArray.getJSONObject(index) ;
					float maxTempC = Float.parseFloat(weatherJSON.getString(ParserConstants.MAXTEMPC)) ;
					float maxTempF = Float.parseFloat(weatherJSON.getString(ParserConstants.MAXTEMPF)) ;
					float minTempC = Float.parseFloat(weatherJSON.getString(ParserConstants.MINTEMPC)) ;
					float minTempF = Float.parseFloat(weatherJSON.getString(ParserConstants.MINTEMPF)) ;
					date = weatherJSON.getString(ParserConstants.DATE) ;

					JSONArray hourlyDetails = weatherJSON.getJSONArray(ParserConstants.HOURLY) ;

					if ( null != hourlyDetails ) {

						int hourlyDetailsLength = hourlyDetails.length() ;

						for( int i = 0 ; i < hourlyDetailsLength ; i ++ ) {
							JSONObject hourlyJSON = hourlyDetails.getJSONObject(i) ;
							weatherDto = new Weatherdto() ;
							weatherDto.setDate(date) ;
							weatherDto.setTempInCentigrade(Float.parseFloat(hourlyJSON.getString(ParserConstants.TEMP_C))) ;
							weatherDto.setTempInFahrenheit(Float.parseFloat(hourlyJSON.getString(ParserConstants.TEMP_F))) ;
							weatherDto.setTime(hourlyJSON.getString(ParserConstants.TIME)) ;
							weatherDto.setIsdaytime(hourlyJSON.getString(ParserConstants.IS_DAY_TIME));
							weatherDto.setWindSpeed(Float.parseFloat(hourlyJSON.getString(ParserConstants.WIND_SPEED))) ;
							weatherDto.setWindDirection(hourlyJSON.getString(ParserConstants.WIND_DIRECTION)) ;
							weatherDto.setWeatherDesc(hourlyJSON.getJSONArray(ParserConstants.WEATHER_DESC).optJSONObject(0).getString(ParserConstants.VALUE));
							weatherDto.setMaxTempC(maxTempC) ;
							weatherDto.setMaxTempF(maxTempF) ;
							weatherDto.setMinTempC(minTempC) ;
							weatherDto.setMinTempF(minTempF) ;
							weatherDto.setWindDegree(Float.parseFloat(hourlyJSON.getString(ParserConstants.WIND_DEGREE))) ;
							weatherForecastList.add(weatherDto) ;
						}
					}
				}
			}

		} catch (JSONException e) {
			ALog.e(ALog.PARSER, "JSONException") ;
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
		} catch (JsonSyntaxException e2) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
			return null;
		}
		return weatherForecastList ;
	}

	public static DeviceDto getDeviceDetails(String data) {
		if (data == null || data.isEmpty()) {
			return null;
		}
		Gson gson = new GsonBuilder().create() ;
		DeviceDto deviceDto = null;
		try {
			deviceDto = gson.fromJson(data, DeviceDto.class) ;
		} catch (JsonSyntaxException e) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
		}
		return deviceDto;
	}

	public static DeviceWifiDto getDeviceWifiDetails(String data) {
		if (data == null || data.isEmpty()) {
			return null;
		}
		Gson gson = new GsonBuilder().create() ;
		DeviceWifiDto deviceWifiDto = null;
		try {
			deviceWifiDto = gson.fromJson(data, DeviceWifiDto.class) ;
		} catch (JsonSyntaxException e) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
		} catch (JsonIOException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
		} catch (Exception e2) {
			ALog.e(ALog.PARSER, "Exception");
		}
		return deviceWifiDto;
	}

	//TODO : Unit test.
	public static List<City> parseLocationData(String dataToParse) {
		if(dataToParse == null) {
			return null;
		}
		try {
			CityDetails cities = new GsonBuilder().create().fromJson(dataToParse, CityDetails.class);
			Map<String, City> citiesMap = cities.getCities();
			List<City> citiesList = new ArrayList<City>(citiesMap.values());
			return citiesList;
		} catch (JsonSyntaxException jse) {
			ALog.e(ALog.PARSER, "JsonSyntaxException");
			return null;
		} catch (JsonIOException jioe) {
			ALog.e(ALog.PARSER, "JsonIOException");
			return null;
		} catch (Exception e) {
			ALog.e(ALog.PARSER, "parseLocationData");
			return null;
		}
	}

	public static List<SchedulePortInfo> parseSchedulerDto(String dataToParse) {
		ALog.i(ALog.SCHEDULER, dataToParse) ;
		List<SchedulePortInfo> schedulesList = new ArrayList<SchedulePortInfo>() ;
		JSONObject jsonObject = null ;
		try {			
			jsonObject = new JSONObject(dataToParse);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = jsonObject.keys() ;
			String key = null ;
			while(iterator.hasNext()) {
				key = iterator.next() ;
				SchedulePortInfo schedules = new SchedulePortInfo() ;
				JSONObject schedule;
				schedule = jsonObject.getJSONObject(key);
				schedules.setName((String)schedule.get("name")) ;
				schedules.setScheduleNumber(Integer.parseInt(key)) ;
				schedulesList.add(schedules) ;
			}

		} catch (JSONException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
			schedulesList = null ;
			e.printStackTrace();
		} catch(Exception e) {
			schedulesList = null ;
			e.printStackTrace() ;
		}
		return schedulesList ;
	}
	
	public static List<SchedulePortInfo> parseScheduleListViaCPP(String dataToParse) {
		ALog.i(ALog.SCHEDULER, dataToParse) ;
		List<SchedulePortInfo> schedulesList = new ArrayList<SchedulePortInfo>() ;
		JSONObject jsonObject = null ;
		try {			
			jsonObject = new JSONObject(dataToParse);
			JSONObject dataJson = jsonObject.getJSONObject("data") ;
			
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = dataJson.keys() ;
			String key = null ;
			while(iterator.hasNext()) {
				key = iterator.next() ;
				SchedulePortInfo schedules = new SchedulePortInfo() ;
				JSONObject schedule;
				schedule = dataJson.getJSONObject(key);
				schedules.setName((String)schedule.get("name")) ;
				schedules.setScheduleNumber(Integer.parseInt(key)) ;
				schedulesList.add(schedules) ;
			}

		} catch (JSONException e) {
			ALog.e(ALog.PARSER, "JsonIOException");
			schedulesList = null ;
			e.printStackTrace();
		} catch(Exception e) {
			schedulesList = null ;
			e.printStackTrace() ;
		}
		return schedulesList ;
	}
	
	public static SchedulePortInfo parseScheduleDetails(String dataToParse) {
		SchedulePortInfo schedulePortInfo = new SchedulePortInfo() ;
		try {
			JSONObject scheduleJson = new JSONObject(dataToParse) ;
			schedulePortInfo.setName(scheduleJson.getString("name")) ;
			schedulePortInfo.setEnabled(scheduleJson.getBoolean("enabled")) ;
			schedulePortInfo.setDays(scheduleJson.getString("days")) ;
			schedulePortInfo.setMode(scheduleJson.getJSONObject("command").getString("om")) ;
			schedulePortInfo.setScheduleTime(scheduleJson.getString("time")) ;
 		} catch (JSONException e) {
			schedulePortInfo = null ;
			e.printStackTrace();
		} catch (Exception e) {
			schedulePortInfo = null ;
		}
		return schedulePortInfo ;
	}
	
	public static SchedulePortInfo parseScheduleDetailsFromCPP(String dataToParse) {
		SchedulePortInfo schedulePortInfo = new SchedulePortInfo() ;
		try {
			JSONObject scheduleJson = new JSONObject(dataToParse).getJSONObject("data") ;
			schedulePortInfo.setName(scheduleJson.getString("name")) ;
			schedulePortInfo.setEnabled(scheduleJson.getBoolean("enabled")) ;
			schedulePortInfo.setDays(scheduleJson.getString("days")) ;
			schedulePortInfo.setMode(scheduleJson.getJSONObject("command").getString("om")) ;
			schedulePortInfo.setScheduleTime(scheduleJson.getString("time")) ;
 		} catch (JSONException e) {
			schedulePortInfo = null ;
			e.printStackTrace();
		} catch (Exception e) {
			schedulePortInfo = null ;
		}
		return schedulePortInfo ;
	}
}

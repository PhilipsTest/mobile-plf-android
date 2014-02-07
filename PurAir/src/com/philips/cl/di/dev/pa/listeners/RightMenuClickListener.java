package com.philips.cl.di.dev.pa.listeners;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.constants.ParserConstants;
import com.philips.cl.di.dev.pa.controller.AirPurifierController;
import com.philips.cl.di.dev.pa.dto.AirPurifierEventDto;
import com.philips.cl.di.dev.pa.pureairui.MainActivity;
import com.philips.cl.di.dev.pa.util.AppConstants;

public class RightMenuClickListener implements OnClickListener {
	
	private AirPurifierController airPurifierController ;
	private static final String TAG = RightMenuClickListener.class.getSimpleName();
	
	private Context context;
	
	private Activity activity;
	
	private TextView autoText;
	
	private View fanSpeedBackground, timerBackground;
	
	//Control panel buttons
	private Button fanSpeed, timer, schedule;
	private ToggleButton power, childLock, indicatorLight;
	
	//On/off states for power, child lock and indicator light.
	private boolean isPowerOn, isChildLockOn, isIndicatorLightOn;
	
	//Fan speed menu buttons
	private Button fanSpeedSilent, fanSpeedTurbo, fanSpeedOne, fanSpeedTwo, fanSpeedThree;
	private ToggleButton fanSpeedAuto;
	
	//Timer buttons
	private Button timerOff, timerTwoHours, timerFourHours, timerEightHours;
	
	private boolean isFanSpeedMenuVisible, isTimerMenuVisible, isFanSpeedAuto;
	
	public RightMenuClickListener(Context context) {
		this.context = context;
		this.activity = (Activity) context;
		
		isPowerOn = false;
		isChildLockOn = false;
		isIndicatorLightOn = false;
		
		isFanSpeedMenuVisible = false;
		isTimerMenuVisible = false;
		
		power = (ToggleButton) activity.findViewById(R.id.btn_rm_power);
		schedule = (Button) activity.findViewById(R.id.btn_rm_scheduler);
		childLock = (ToggleButton) activity.findViewById(R.id.btn_rm_child_lock);		
		indicatorLight = (ToggleButton) activity.findViewById(R.id.btn_rm_indicator_light);
		
		fanSpeed = (Button) activity.findViewById(R.id.btn_rm_fan_speed);
		
		fanSpeedSilent = (Button) activity.findViewById(R.id.fan_speed_silent);
		fanSpeedAuto = (ToggleButton) activity.findViewById(R.id.fan_speed_auto);
		autoText = (TextView) activity.findViewById(R.id.tv_fan_speed_auto);
		fanSpeedBackground = activity.findViewById(R.id.background_fan_speed);
		fanSpeedTurbo = (Button) activity.findViewById(R.id.fan_speed_turbo);
		fanSpeedOne = (Button) activity.findViewById(R.id.fan_speed_one);
		fanSpeedTwo = (Button) activity.findViewById(R.id.fan_speed_two);
		fanSpeedThree = (Button) activity.findViewById(R.id.fan_speed_three);
		
		timer = (Button) activity.findViewById(R.id.btn_rm_set_timer);
		timerBackground = activity.findViewById(R.id.background_timer);
		timerOff = (Button) activity.findViewById(R.id.timer_off);
		timerTwoHours = (Button) activity.findViewById(R.id.one_hour);
		timerFourHours = (Button) activity.findViewById(R.id.four_hours);
		timerEightHours = (Button) activity.findViewById(R.id.eight_hours);
		
		this.airPurifierController = new AirPurifierController(context) ;
 		
	}
	
	/**
	 * 
	 *  public void setBackground(Drawable background) {
     *		//noinspection deprecation
     *		setBackgroundDrawable(background);
	 *	}
	 *  
	 *  setBackground(Drawable drawable) is supported from API 16 onwards.
	 *  And it calls setBackgroundDrawable internally.
	 *  
	 * @param airPurifierEventDto
	 */
	
	public void setSensorValues(AirPurifierEventDto airPurifierEventDto) {
//		Log.i(TAG, "setSensorValues fan speed " + Utils.getFanSpeedText(airPurifierEventDto.getFanSpeed()) + " dto fan speed " + airPurifierEventDto.getFanSpeed());
		Log.i(TAG, "setSensorValues " + airPurifierEventDto.getPowerMode());
		if( airPurifierEventDto.getPowerMode().equals(AppConstants.POWER_ON)) {
			power.setChecked(getPowerButtonState(airPurifierEventDto));
			setFanSpeed(airPurifierEventDto);
			timer.setText(getTimerText(airPurifierEventDto));
			schedule.setText("N.A.");
			childLock.setChecked(getOnOffStatus(airPurifierEventDto.getChildLock()));
			indicatorLight.setChecked(getOnOffStatus(airPurifierEventDto.getAqiL()));
		}
		else {
			power.setChecked(getPowerButtonState(airPurifierEventDto));
			disableControlPanelButtonsOnPowerOff() ;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setFanSpeed(AirPurifierEventDto airPurifierEventDto) {
		String fanSpeedText = airPurifierEventDto.getFanSpeed();
		Drawable buttonImage = null;
		
//		Log.i(TAG, "setFanSpeed " + fanSpeedText);
//		fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
		isFanSpeedAuto = false;
		if(AppConstants.FAN_SPEED_SILENT.equals(fanSpeedText)) {
			fanSpeed.setText("Silent");
			buttonImage = context.getResources().getDrawable(R.drawable.button_blue_bg_2x);
		} else if(AppConstants.FAN_SPEED_TURBO.equals(fanSpeedText)) {
			fanSpeed.setText("Turbo");
			buttonImage = context.getResources().getDrawable(R.drawable.button_blue_bg_2x);
		} else if(AppConstants.FAN_SPEED_AUTO.equals(fanSpeedText)) {
			fanSpeed.setText("Auto");
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on));
			buttonImage = context.getResources().getDrawable(R.drawable.button_blue_bg_2x);
			isFanSpeedAuto = true;
		} else if(AppConstants.FAN_SPEED_ONE.equals(fanSpeedText)) {
			fanSpeed.setText("");
			buttonImage = context.getResources().getDrawable(R.drawable.fan_speed_one);
		} else if(AppConstants.FAN_SPEED_TWO.equals(fanSpeedText)) {
//			Log.i(TAG, "setFanSpeed (AppConstants.FAN_SPEED_TWO.equals(fanSpeedText)");
			fanSpeed.setText("");
			buttonImage = context.getResources().getDrawable(R.drawable.fan_speed_two);
		} else if(AppConstants.FAN_SPEED_THREE.equals(fanSpeedText)) {
			fanSpeed.setText("");
			buttonImage = context.getResources().getDrawable(R.drawable.fan_speed_three);
		}
		fanSpeed.setBackgroundDrawable(buttonImage);
	}

	private boolean getPowerButtonState(AirPurifierEventDto airPurifierEventDto) {
		Drawable powerState = null;
//		Log.i(TAG, "getPowerButtonState airPurifierDTO " + (airPurifierEventDto == null));
		if(airPurifierEventDto == null) {
			powerState = context.getResources().getDrawable(R.drawable.switch_off);
			disableControlPanelButtonsOnPowerOff();
			isPowerOn = false;
			return false;
		}
		String powerMode = airPurifierEventDto.getPowerMode();
		if(powerMode != null && powerMode.equals(AppConstants.POWER_ON)) {
			powerState = context.getResources().getDrawable(R.drawable.switch_on);
			enableButtonsOnPowerOn(MainActivity.getAirPurifierEventDto());
			isPowerOn = true;
		} else {
			powerState = context.getResources().getDrawable(R.drawable.switch_off);
			disableControlPanelButtonsOnPowerOff();
			isPowerOn = false;
		}
		
//		Log.i(TAG, "isPowerOn " + isPowerOn);
		
		return isPowerOn;
	}

	private String getTimerText(AirPurifierEventDto airPurifierEventDto) {
		int timeRemaining = airPurifierEventDto.getDtrs();
		if(timeRemaining > 0 && timeRemaining <= 3600) {
			return context.getString(R.string.onehour);
		} else if (timeRemaining > 3600 && timeRemaining <= 14400){
			return context.getString(R.string.fourhour);
		} else if (timeRemaining > 14400 && timeRemaining <= 28800){
			return context.getString(R.string.eighthour);
		} else {
			return context.getString(R.string.off);
		}
	}
	
	private boolean getOnOffStatus(int status) {
//		Log.i(TAG, "getOnOffStatus " + status);
		if(status == AppConstants.ON) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect:
			Toast.makeText(context, "Connect", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_rm_power:
//			Log.i(TAG, "power is on :: " + isPowerOn);
			if(!isPowerOn) {				
//				power.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on));
				power.setChecked(true);
				enableButtonsOnPowerOn(MainActivity.getAirPurifierEventDto());
				controlDevice(ParserConstants.POWER_MODE, "1") ;
				
			} else {
				power.setChecked(false);
//				power.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
				disableControlPanelButtonsOnPowerOff();
				controlDevice(ParserConstants.POWER_MODE, "0") ;
			}
			isPowerOn = !isPowerOn;
			collapseFanSpeedMenu(true);
			collapseTimerMenu(true);
			break;
		case R.id.btn_rm_child_lock:
			if(!isChildLockOn) {
				childLock.setChecked(true);
//				childLock.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on));
				controlDevice(ParserConstants.CL, "1") ;
			} else {
				childLock.setChecked(false);
//				childLock.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
				controlDevice(ParserConstants.CL, "0") ;
			}
			isChildLockOn = !isChildLockOn;
			collapseFanSpeedMenu(true);
			collapseTimerMenu(true);
			break;
		case R.id.btn_rm_indicator_light:
			if(!isIndicatorLightOn) {
				indicatorLight.setChecked(true);
//				indicatorLight.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on));
				controlDevice(ParserConstants.AQI_LIGHT, "1") ;
			} else {
//				indicatorLight.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
				indicatorLight.setChecked(false);
				controlDevice(ParserConstants.AQI_LIGHT, "0") ;
			}
			isIndicatorLightOn = !isIndicatorLightOn;
			collapseFanSpeedMenu(true);
			collapseTimerMenu(true);
			break;
		case R.id.btn_rm_fan_speed :
			collapseFanSpeedMenu(isFanSpeedMenuVisible);
			collapseTimerMenu(true);
			break;
		case R.id.fan_speed_silent:		
			fanSpeed.setText(((Button) v).getText());
			fanSpeed.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_blue_bg_2x));
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			isFanSpeedAuto = false;
			collapseFanSpeedMenu(true);
			controlDevice(ParserConstants.FAN_SPEED, "s") ;
			break;
		case R.id.fan_speed_auto:
			fanSpeed.setText(((Button) v).getText());
			if(!isFanSpeedAuto) {
//				fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on));
				controlDevice(ParserConstants.FAN_SPEED, "a") ;
			} else {
//				fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
				controlDevice(ParserConstants.FAN_SPEED, "1") ;
			}
			isFanSpeedAuto = !isFanSpeedAuto;
			collapseFanSpeedMenu(true);
			collapseTimerMenu(true);
			break;
		case R.id.fan_speed_turbo:			
			fanSpeed.setText(((Button) v).getText());
			fanSpeed.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_blue_bg_2x));
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			isFanSpeedAuto = false;
			collapseFanSpeedMenu(true);
			controlDevice(ParserConstants.FAN_SPEED, "t") ;
			break;
		case R.id.fan_speed_one:
			fanSpeed.setText(((Button) v).getText());
			fanSpeed.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.fan_speed_one));
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			isFanSpeedAuto = false;
			collapseFanSpeedMenu(true);
			controlDevice(ParserConstants.FAN_SPEED, "1") ;
			break;
		case R.id.fan_speed_two:
			fanSpeed.setText(((Button) v).getText());
			fanSpeed.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.fan_speed_two));
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			isFanSpeedAuto = false;
			collapseFanSpeedMenu(true);
			controlDevice(ParserConstants.FAN_SPEED, "2") ;
			break;
		case R.id.fan_speed_three:
			fanSpeed.setText(((Button) v).getText());
			fanSpeed.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.fan_speed_three));
//			fanSpeedAuto.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			isFanSpeedAuto = false;
			collapseFanSpeedMenu(true);
			controlDevice(ParserConstants.FAN_SPEED, "3") ;
			break;
			
		case R.id.btn_rm_set_timer:
			collapseTimerMenu(isTimerMenuVisible);
			collapseFanSpeedMenu(true);
			break;
		case R.id.timer_off:
			timer.setText(((Button) v).getText());
			collapseTimerMenu(true);
			controlDevice(ParserConstants.DEVICE_TIMER, "0") ;
			break;
		case R.id.one_hour:
			timer.setText(((Button) v).getText());
			collapseTimerMenu(true);
			controlDevice(ParserConstants.DEVICE_TIMER, "1") ;
			break;
		case R.id.four_hours:
			timer.setText(((Button) v).getText());
			collapseTimerMenu(true);
			controlDevice(ParserConstants.DEVICE_TIMER, "4") ;
			break;
		case R.id.eight_hours:
			timer.setText(((Button) v).getText());
			collapseTimerMenu(true);
			controlDevice(ParserConstants.DEVICE_TIMER, "8") ;
			break;
		case R.id.btn_rm_scheduler:
			collapseFanSpeedMenu(true);
			collapseTimerMenu(true);
		default:
			break;
		}
	}
	
	private void controlDevice(String key, String value) {
		if ( MainActivity.getAirPurifierEventDto().getConnectionStatus() == com.philips.cl.di.dev.pa.constants.AppConstants.CONNECTED) {
			airPurifierController.setDeviceDetailsLocally(key, value) ;
		}
		else if ( MainActivity.getAirPurifierEventDto().getConnectionStatus() == com.philips.cl.di.dev.pa.constants.AppConstants.CONNECTED_VIA_PHILIPS) {
				airPurifierController.setDeviceDetailsRemotely(key, value) ;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void disableControlPanelButtonsOnPowerOff() {
		Log.i(TAG, "disableControlPanelButtonsOnPowerOff");
		Drawable disabledButton = context.getResources().getDrawable(R.drawable.button_bg_2x);
		disabledButton.setAlpha(100);
		
		fanSpeed.setClickable(false);
		fanSpeed.setBackgroundDrawable(disabledButton);
		
		timer.setClickable(false);
		timer.setBackgroundDrawable(disabledButton);
		
		schedule.setClickable(false);
		schedule.setBackgroundDrawable(disabledButton);
		
		childLock.setClickable(false);
//		childLock.setBackgroundDrawable(disabledButton);
		childLock.setChecked(false);
		
		indicatorLight.setClickable(false);
//		indicatorLight.setBackgroundDrawable(disabledButton);
		indicatorLight.setChecked(false);
		
		collapseFanSpeedMenu(true);
		collapseTimerMenu(true);
	}
	
	@SuppressWarnings("deprecation")
	private void enableButtonsOnPowerOn(AirPurifierEventDto airPurifierEventDto) {
		Drawable enabledButton = context.getResources().getDrawable(R.drawable.button_blue_bg_2x);
		
		fanSpeed.setClickable(true);
		setFanSpeed(airPurifierEventDto);
		
		timer.setClickable(true);
		timer.setBackgroundDrawable(enabledButton);
		
		schedule.setClickable(true);
		schedule.setBackgroundDrawable(enabledButton);
		
//		Log.i(TAG, "enableOtherButtons " + (airPurifierEventDto == null));
		
		if(airPurifierEventDto != null) {
			childLock.setClickable(true);
			childLock.setChecked(getOnOffStatus(airPurifierEventDto.getChildLock()));
//			childLock.setChecked(true);
			
			indicatorLight.setClickable(true);
			indicatorLight.setChecked(getOnOffStatus(airPurifierEventDto.getAqiL()));
//			indicatorLight.setChecked(true);
		} else {
			childLock.setClickable(false);
//			childLock.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			childLock.setChecked(false);
			
			indicatorLight.setClickable(false);
//			indicatorLight.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off));
			indicatorLight.setChecked(false);
		}
	}
	
	/**
	 * @param if true, collapse the list
	 *					else expand it. 
	 */
	private void collapseTimerMenu(boolean collapse) {
		if(!collapse) {
			isTimerMenuVisible = !collapse;
			timerBackground.setVisibility(View.VISIBLE);
			timerOff.setVisibility(View.VISIBLE);
			timerTwoHours.setVisibility(View.VISIBLE);
			timerFourHours.setVisibility(View.VISIBLE);
			timerEightHours.setVisibility(View.VISIBLE);
		} else {
			isTimerMenuVisible = !collapse;
			timerBackground.setVisibility(View.GONE);
			timerOff.setVisibility(View.GONE);
			timerTwoHours.setVisibility(View.GONE);
			timerFourHours.setVisibility(View.GONE);
			timerEightHours.setVisibility(View.GONE);
		}
	}

	/**
	 * @param  if true, collapse the list
	 *					else expand it. 
	 */
	private void collapseFanSpeedMenu(boolean collapse) {
		if(!collapse) {
			isFanSpeedMenuVisible = !collapse;
			fanSpeedSilent.setVisibility(View.VISIBLE);
			fanSpeedAuto.setVisibility(View.VISIBLE);
			autoText.setVisibility(View.VISIBLE);
			fanSpeedBackground.setVisibility(View.VISIBLE);
			fanSpeedTurbo.setVisibility(View.VISIBLE);
			fanSpeedOne.setVisibility(View.VISIBLE);
			fanSpeedTwo.setVisibility(View.VISIBLE);
			fanSpeedThree.setVisibility(View.VISIBLE);
		} else {
			isFanSpeedMenuVisible = !collapse;
			fanSpeedSilent.setVisibility(View.GONE);
			fanSpeedAuto.setVisibility(View.GONE);
			autoText.setVisibility(View.GONE);
			fanSpeedBackground.setVisibility(View.GONE);
			fanSpeedTurbo.setVisibility(View.GONE);
			fanSpeedOne.setVisibility(View.GONE);
			fanSpeedTwo.setVisibility(View.GONE);
			fanSpeedThree.setVisibility(View.GONE);
		}
	}
	
	/**
	 * @param viewGroup loops through the entire view group and adds
	 * 					an onClickListerner to the buttons.
	 */
	public void setAllButtonListener(ViewGroup viewGroup) {

	    View v;
	    for (int i = 0; i < viewGroup.getChildCount(); i++) {
	        v = viewGroup.getChildAt(i);
	        if (v instanceof ViewGroup) {
	            setAllButtonListener((ViewGroup) v);
	        } else if (v instanceof Button) {
	            ((Button) v).setOnClickListener(this);
	        }
	    }
	}

	@SuppressWarnings("deprecation")
	public void disableControlPanel(boolean connected, AirPurifierEventDto airPurifierEventDto) {
//		Log.i(TAG, "disableControlPanel connected " + connected);
		Drawable powerButton = null;
		if(!connected) {
			power.setClickable(false);
			powerButton = context.getResources().getDrawable(R.drawable.switch_off);
			powerButton.setAlpha(100);
//			power.setBackgroundDrawable(powerButton);
//			power.setBackgroundResource(R.drawable.switch_off);
			power.setChecked(false);
			disableControlPanelButtonsOnPowerOff();
		} else {
			power.setClickable(true);
			
			power.setChecked(getPowerButtonState(airPurifierEventDto));
			if(isPowerOn)
				enableButtonsOnPowerOn(airPurifierEventDto);
		}
		
	}
	
}

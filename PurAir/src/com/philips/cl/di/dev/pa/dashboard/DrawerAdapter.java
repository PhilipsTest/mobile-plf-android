package com.philips.cl.di.dev.pa.dashboard;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;

import com.philips.cl.di.dev.pa.util.ALog;

public class DrawerAdapter implements DrawerListener {
	
	public enum DrawerEvent { DRAWER_OPENED, DRAWER_CLOSED, DRAWER_SLIDE_END }
	
	private static DrawerAdapter smInstance;
	
	private List<DrawerEventListener> listeners;

	private DrawerAdapter() {
		listeners = new ArrayList<DrawerEventListener>();
	}
	
	public static DrawerAdapter getInstance() {
		if(smInstance == null) {
			smInstance = new DrawerAdapter();
		}
		return smInstance;
	}
	
	public void addDrawerListener(DrawerEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeDrawerListener(DrawerEventListener listener) {
		ALog.i(ALog.TEMP, "DrawerAdapter#removeDrawerListener listener " + listener);
		listeners.remove(listener);
	}
	
	private void notifyListeners(DrawerEvent event, View drawerView) {
		for(DrawerEventListener listener : listeners) {
			listener.onDrawerEvent(event, drawerView);
		}
	}
	
	@Override
	public void onDrawerClosed(View drawerView) {
		ALog.i(ALog.TEMP, "DrawerAdapter$onDrawerClosed");
		notifyListeners(DrawerEvent.DRAWER_CLOSED, drawerView);
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		ALog.i(ALog.TEMP, "DrawerAdapter$onDrawerOpened");
		notifyListeners(DrawerEvent.DRAWER_OPENED, drawerView);
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		if(slideOffset <= 0) {
			notifyListeners(DrawerEvent.DRAWER_SLIDE_END, drawerView);
		}
	}

	@Override
	public void onDrawerStateChanged(int newState) {
		ALog.i(ALog.TEMP, "DrawerAdapter$onDrawerStateChanged newState " + newState);
	}
	
	public interface DrawerEventListener {
		void onDrawerEvent(DrawerEvent event, View drawerView);
	}
}

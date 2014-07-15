package com.philips.cl.di.dev.pa.scheduler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.fragment.BaseFragment;
import com.philips.cl.di.dev.pa.scheduler.SchedulerConstants.SchedulerID;
import com.philips.cl.di.dev.pa.util.ALog;

public class RepeatFragment extends BaseFragment implements SchedulerRepeatListener {
	
	private ListView lstDays;
	private String[] days;
	private boolean[] daysSelected = {false, false, false, false, false, false, false};
	private RepeatAdapter repeatAdapter;
	private StringBuilder sSelectedDays;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ALog.i(ALog.SCHEDULER, "RepeatFragment::onCreateView() method enter");
		View view = inflater.inflate(R.layout.repeat_scheduler, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((SchedulerActivity) getActivity()).setActionBar(SchedulerID.REPEAT);
		lstDays = (ListView) getView().findViewById(R.id.repeat_scheduler);
		days = getResources().getStringArray(R.array.weekday_array);
		String selectedDays = getArguments().getString(SchedulerConstants.DAYS);
		daysSelected = SchedulerUtil.getSelectedDayList(days, selectedDays);
		repeatAdapter = new RepeatAdapter(getActivity(), R.layout.repeat_scheduler_item, days, daysSelected, this);
		lstDays.setAdapter(repeatAdapter);
		((SchedulerActivity)getActivity()).setDays(setDaysString());
	}
	
	private String setDaysString() {
		ALog.i(ALog.SCHEDULER, "RepeatFragment::setDaysString() method enter");
		sSelectedDays = new StringBuilder();
		for (int i=0; i < daysSelected.length; i++) {
			if (daysSelected[i]) {
				sSelectedDays = sSelectedDays.append(i);
			}
		}
		return sSelectedDays.toString();
	}

	@Override
	public void onItemClick(boolean[] selectedItems) {
		daysSelected = selectedItems;
		((SchedulerActivity)getActivity()).setDays(setDaysString());
	}
}

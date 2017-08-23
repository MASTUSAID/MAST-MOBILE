package com.rmsi.android.mast.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceResultReceiver extends ResultReceiver {

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);

	}

	private Receiver mReceiver;

	public ServiceResultReceiver(Handler handler) {
		super(handler);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {

		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultData);
		}
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

}
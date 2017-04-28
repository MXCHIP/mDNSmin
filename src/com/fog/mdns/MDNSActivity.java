package com.fog.mdns;

import org.json.JSONArray;

import io.fogcloud.fog_mdns.api.MDNS;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MDNSActivity extends Activity {
	private String TAG = "---main---";
	
	private String _SERV_NAME = "_easylink._tcp.local.";
	private final int _EL_S = 1;
	private final int _EL_F = 2;
	private boolean _ISSTART = false;
	
	private Context context;
	
	private TextView startmdns;
	private TextView logsid;
	
	private MDNS mdns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MDNSActivity.this;

		initView();
		mdns = new MDNS(context);

		initOnClick();
	}

	private void initView() {
		startmdns = (TextView) findViewById(R.id.startmdns);
		logsid = (TextView) findViewById(R.id.logsid);
	}
	
	private void initOnClick(){
		startmdns.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTag();
			}
		});
	}
	
	/**
	 * 设置TAG标记按钮
	 */
	private void setTag() {
		if (!_ISSTART) {
			startmdns.setBackgroundResource(R.color.red);
			startmdns.setText(R.string.stop_mdns);
			startMDNS();
		} else {
			startmdns.setBackgroundResource(R.color.blue_btn);
			startmdns.setText(R.string.start_mdns);
			stopMDNS();
		}
		_ISSTART = !_ISSTART;
	}
	
	private void startMDNS(){
		mdns.startSearchDevices(_SERV_NAME, new SearchDeviceCallBack() {
			@Override
			public void onSuccess(int code, String message) {
				super.onSuccess(code, message);
				send2handler(_EL_S, message);
			}
			
			@Override
			public void onFailure(int code, String message) {
				super.onFailure(code, message);
				send2handler(_EL_F, message);
			}
			
			@Override
			public void onDevicesFind(int code, JSONArray deviceStatus) {
				super.onDevicesFind(code, deviceStatus);
				send2handler(_EL_S, deviceStatus.toString());
			}
		});
	}
	
	private void stopMDNS(){
		mdns.stopSearchDevices(new SearchDeviceCallBack() {
			@Override
			public void onSuccess(int code, String message) {
				super.onSuccess(code, message);
				send2handler(_EL_S, message);
			}
		});
	}
	
	/**
	 * 发送消息给handler
	 * 
	 * @param tag
	 * @param message
	 */
	private void send2handler(int tag, String message) {
		Message msg = new Message();
		msg.what = tag;
		msg.obj = message;
		elhandler.sendMessage(msg);
	}
	
	/**
	 * 监听配网时候调用接口的log，并显示在activity上
	 */
	Handler elhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case _EL_S:
				logsid.append("\n" + msg.obj.toString());
				break;
			case _EL_F:
				logsid.append("\n" + msg.obj.toString());
				break;
			}
		};
	};
}

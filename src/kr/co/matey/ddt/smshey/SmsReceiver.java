package kr.co.matey.ddt.smshey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	static final String logTag = "SmsReceiver";
	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	static final String number = "01012345678";
	
	private HttpClient httpclient;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            Object messages[] = (Object[]) bundle.get("pdus");
    		SmsMessage smsMessage[] = new SmsMessage[messages.length];
    		for (int n = 0; n < messages.length; n++) {
    			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
    			if (number.equals(smsMessage[n].getOriginatingAddress())) {
    				Map<String, String> params = new HashMap<String, String>();
    				
    				callWebServer("http://google.com", params, "GET");
    			}
    		}
        }
	}
	
	private void callWebServer(String url, Map<String, String> params, String method) {
		try {
			if ("GET".equals(method)) {
				String html = getHtml(url);
				Log.d("SmsHey", html);
			} else {
				//TODO: POST
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//from hycuapp
	private String getHtml(String url) throws ClientProtocolException, IOException {
		if (httpclient == null) httpclient = new DefaultHttpClient();
		
		HttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		response = httpclient.execute(httpget);
		InputStream is = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "euc-kr"), 4096);
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		is.close();
		return sb.toString();
	}
}

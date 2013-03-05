package com.josegd.monthcalwidget;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class MCWInstructions extends Activity {

	private TextView tvMoreInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_instructions);
		tvMoreInfo = (TextView) findViewById(R.id.more_info);
		tvMoreInfo.setMovementMethod(LinkMovementMethod.getInstance());
		tvMoreInfo.setText(Html.fromHtml(getString(R.string.more_info)));
	}
	
}

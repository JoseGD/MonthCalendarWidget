package com.josegd.monthcalwidget;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MCWInstructions extends Activity {

	private TextView tvMoreHelp;
	private ScrollView svInstructions;
	private Button btnShowInstructions;
	private int buttonWidth;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_instructions);
		tvMoreHelp = (TextView) findViewById(R.id.instr_morehelp);
		svInstructions = (ScrollView) findViewById(R.id.instr_howtouse_sv);
		btnShowInstructions = (Button) findViewById(R.id.showinstr_btn);
		tvMoreHelp.setMovementMethod(LinkMovementMethod.getInstance());
		tvMoreHelp.setText(Html.fromHtml(getString(R.string.instructions_morehelp)));
		buttonWidth = getResources().getDimensionPixelSize(R.dimen.instr_textsize_1);
	}

	public void showInstructions(View view) {
		if (svInstructions.getVisibility() == View.VISIBLE) {
			svInstructions.setVisibility(View.INVISIBLE);
			btnShowInstructions.setText(R.string.show_instructions);
			btnShowInstructions.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonWidth);
		} else {
			svInstructions.setVisibility(View.VISIBLE);
			btnShowInstructions.setText(R.string.instructions_shown);
			btnShowInstructions.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonWidth - 6);
		}
	}
	
}

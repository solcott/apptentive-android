/*
 * RatingController.java
 *
 * Created by Sky Kelsey on 2011-09-17.
 * Copyright 2011 Apptentive, Inc. All rights reserved.
 */

package com.apptentive.android.sdk.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.apptentive.android.sdk.ALog;
import com.apptentive.android.sdk.Apptentive;
import com.apptentive.android.sdk.R;

public class RatingController {

	private static ALog log = new ALog(FeedbackController.class);

	private Activity activity;
	private Dialog dialog;

	public RatingController(Activity activity) {
		this.activity = activity;
	}

	private void setupForm() {
		dialog.setTitle("Rate " + ApptentiveModel.getInstance().getAppDisplayName() + "?");

		ApptentiveModel model = ApptentiveModel.getInstance();
		Display display = activity.getWindowManager().getDefaultDisplay();
		int width = new Float(display.getWidth() * 0.8f).intValue();

		TextView message = (TextView) dialog.findViewById(R.id.apptentive_rating_message);
		message.setWidth(width);
		message.setText(String.format(activity.getString(R.string.apptentive_rating_message), model.getAppDisplayName()));
		Button rate = (Button) dialog.findViewById(R.id.apptentive_rating_rate);
		rate.setOnClickListener(clickListener);
		rate.setText(String.format(activity.getString(R.string.apptentive_rating_rate), model.getAppDisplayName()));
		Button later = (Button) dialog.findViewById(R.id.apptentive_rating_later);
		later.setOnClickListener(clickListener);
		Button no = (Button) dialog.findViewById(R.id.apptentive_rating_no);
		no.setOnClickListener(clickListener);
	}

	public void show() {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View content = inflater.inflate(R.layout.apptentive_rating, null, false);
		dialog = new Dialog(activity);

		dialog.setContentView(content);
		setupForm();
		dialog.show();
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dialog.dismiss();
			switch (view.getId()) {
				case R.id.apptentive_rating_rate:
					try{
						activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ApptentiveModel.getInstance().getAppPackage())));
						Apptentive.getInstance().ratingYes();
					}catch(ActivityNotFoundException e) {
						final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
						alertDialog.setTitle("Oops!");
						alertDialog.setMessage(activity.getString(R.string.apptentive_rating_no_market));
						alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
					}finally{
						dialog.dismiss();
					}
					break;
				case R.id.apptentive_rating_later:
					Apptentive.getInstance().ratingRemind();
					break;
				case R.id.apptentive_rating_no:
					Apptentive.getInstance().ratingNo();
					break;
				default:
					break;
			}
		}
	};
}

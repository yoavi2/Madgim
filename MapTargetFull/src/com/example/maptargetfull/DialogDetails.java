package com.example.maptargetfull;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogDetails extends DialogFragment {

	private ArrayList mSelectedItems = new ArrayList();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// mSelectedItems = new ArrayList(); // Where we track the selected
		// items
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Choose what you want to see")
				.setMultiChoiceItems(R.array.list, ischecked(),
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									// If the user checked the item, add it to
									// the selected items
									mSelectedItems.add(which);
								} else if (mSelectedItems.contains(which)) {
									// Else, if the item is already in the
									// array, remove it
									mSelectedItems.remove(Integer
											.valueOf(which));
								}
							}
						})
				// Set the action buttons
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK, so save the mSelectedItems results
						// somewhere
						// or return them to the component that opened the
						// dialog

						if (mSelectedItems.contains(0)) {
							GlobalParams.getInstance().setFriend(true);
						} else {
							GlobalParams.getInstance().setFriend(false);
						}
						if (mSelectedItems.contains(1)) {
							GlobalParams.getInstance().setEnemy(true);
						} else {
							GlobalParams.getInstance().setEnemy(false);
						}
						GlobalParams.getInstance().getView()
								.setWillNotDraw(false);
						GlobalParams.getInstance().getView().postInvalidate();
					}
				})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});

		// }; {
		// public void onClick(DialogInterface dialog, int id) {
		// // FIRE ZE MISSILES!
		// }
		// })
		// .setNegativeButton("please don't", new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User cancelled the dialog
		// }
		// });
		// Create the AlertDialog object and return it
		return builder.create();
	}

	public boolean[] ischecked() {
		boolean[] array = new boolean[2];
		if (mSelectedItems.contains(0)) {
			array[0] = true;
			// Arrays.fill(array, true);
		} else {
			array[0] = false;
			// Arrays.fill(array, false);

		}
		if (mSelectedItems.contains(1)) {
			array[1] = true;
			// Arrays.fill(array, true);
		} else {
			array[1] = false;
			// Arrays.fill(array, false);

		}

		return array;
	}
}

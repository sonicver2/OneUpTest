package com.christofan.oneuptest.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.christofan.oneuptest.R;

public class PaymentCompleteDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_payment_complete_text)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.dialog_payment_complete_positive,(DialogInterface.OnClickListener)getActivity())
                .setNegativeButton(R.string.dialog_payment_complete_negative,(DialogInterface.OnClickListener)getActivity());
        return builder.create();
    }
}

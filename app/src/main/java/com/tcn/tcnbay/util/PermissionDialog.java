package com.tcn.tcnbay.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tcn.tcnbay.R;
import com.tcn.tcnbay.interfaces.IDialogCallback;

public class PermissionDialog extends DialogFragment {

    private IDialogCallback callback;

    public void setCallback(IDialogCallback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.permission_not_granted)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onDialogOk();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onDialogCancel();
                    }
                });
        return builder.create();
    }
}


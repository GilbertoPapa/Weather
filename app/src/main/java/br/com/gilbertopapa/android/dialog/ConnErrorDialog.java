package br.com.gilbertopapa.android.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

import br.com.gilbertopapa.android.R;

// Dialog que exibe o erro de conexão
public class ConnErrorDialog extends DialogFragment {
	public static final String TAG = "connErrorDialog";
	
	// Método para exibição do dialog
	public static void show(FragmentManager fm) {
		ConnErrorDialog dialog = new ConnErrorDialog();
		dialog.show(fm, TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.dialog_conn_msg)
			.setMessage(R.string.dialog_conn_message)
			.setPositiveButton(R.string.btn_fechar, null)
			.create();
	}
}

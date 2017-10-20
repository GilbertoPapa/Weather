package br.com.gilbertopapa.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// Utilitários associados à conectividade
public class ConnectivityUtils {

	// Verifica se existe uma conexão de dados ativa
	// Exige a permissão android.permission.ACCESS_NETWORK_STATE
	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

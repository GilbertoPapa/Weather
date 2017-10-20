package br.com.gilbertopapa.android.model;

import java.io.Serializable;

import org.json.JSONObject;

// Representa uma temperatura
public class Temperatura implements Serializable {

	private int min;
	private int max;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	// Cria um objeto Temperatura a partir de um JSONObject
	public static Temperatura createFromJSON(JSONObject jsonObj) {
		Temperatura temperatura = new Temperatura();
		temperatura.min = jsonObj.optInt("min", -1);
		temperatura.max = jsonObj.optInt("max", -1);
		return temperatura;
	}
}

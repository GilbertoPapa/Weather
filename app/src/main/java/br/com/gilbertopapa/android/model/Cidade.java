package br.com.gilbertopapa.android.model;

import java.io.Serializable;

import org.json.JSONObject;

// Representa uma cidade
public class Cidade implements Comparable<Cidade>, Serializable {

	private int id;
	private String nome;
	private String pais;
	private Temperatura temperatura;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public Temperatura getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(Temperatura temperatura) {
		this.temperatura = temperatura;
	}

	// Cria um objeto Cidade a partir de um JSONObject
	public static Cidade createFromJSON(JSONObject jsonObj) {
		Cidade cidade = new Cidade();
		cidade.id = jsonObj.optInt("id", -1);
		cidade.nome = jsonObj.optString("nome", null);
		cidade.pais = jsonObj.optString("pais", null);
		return cidade;
	}

	@Override
	public int compareTo(Cidade another) {
		return this.nome.compareTo(another.nome);
	}
}

package br.com.gilbertopapa.android.webservice;

import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.gilbertopapa.android.model.Cidade;
import br.com.gilbertopapa.android.model.Temperatura;
import br.com.gilbertopapa.android.utils.Constants;

// Proxy para acesso ao web service
public class WebServiceProxy {
	private static final Uri WEB_SERVICE_CONTENT = Uri.parse("http://code.softblue.com.br:8080/web/rest/weather");

	private HttpClient http = new DefaultHttpClient();

	// Obtém a lista de cidades
	public List<Cidade> listCidades() throws IOException, WebServiceException {
		try {
			// http://code.gilbertopapa.com.br:8080/web/rest/weather
			HttpGet get = new HttpGet(WEB_SERVICE_CONTENT.toString());

			// Executa uma operação GET
			HttpResponse response = http.execute(get);
			
			String responseStr = getResponseAsString(response);

			// Cria um JSONArray com base na resposta
			JSONArray array = new JSONArray(responseStr);

			List<Cidade> cidades = new ArrayList<>();

			// Itera sobre os elementos do array, criando as cidades
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObj = array.getJSONObject(i);
				cidades.add(Cidade.createFromJSON(jsonObj));
			}

			return cidades;
		} catch (JSONException e) {
			throw new WebServiceException("Erro ao processar JSON", e);
		}
	}
	
	// Obtém a temperatura de uma cidade específica
	public Temperatura obterTemperatura(int cidadeId) throws IOException, WebServiceException {
		try {
			// Coloca o ID no final da URL do web service
			// http://code.gilbertopapa.com.br:8080/web/rest/weather/#
			Uri uri = ContentUris.withAppendedId(WEB_SERVICE_CONTENT, cidadeId);
			HttpGet get = new HttpGet(uri.toString());

			// Executa uma operação GET
			HttpResponse response = http.execute(get);
			
			String responseStr = getResponseAsString(response);

			// Cria um JSONObject com base na resposta
			JSONObject jsonObj = new JSONObject(responseStr);
			
			// Cria um objeto temperatura com base no retorno
			return Temperatura.createFromJSON(jsonObj);
		
		} catch (JSONException e) {
			throw new WebServiceException("Erro ao processar JSON", e);
		}
	}

	// Transforma a resposta do HttpClient em uma String
	private String getResponseAsString(HttpResponse response) throws WebServiceException, IOException {
		// Lê o código de retorno do HTTP
		int code = response.getStatusLine().getStatusCode();

		if (code != HttpStatus.SC_OK) {
			// Se o código não for de sucesso (200), lança exceção
			throw new WebServiceException("O HTTP retornou o código de erro " + code);
		}

		// Extrai a string da resposta
		String responseStr = EntityUtils.toString(response.getEntity());
		Log.i(Constants.LOG_TAG, "JSON: " + responseStr);
		
		return responseStr;
	}
}

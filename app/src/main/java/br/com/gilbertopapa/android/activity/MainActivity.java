package br.com.gilbertopapa.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import br.com.gilbertopapa.android.R;
import br.com.gilbertopapa.android.adapter.CidadesAdapter;
import br.com.gilbertopapa.android.dialog.ConnErrorDialog;
import br.com.gilbertopapa.android.fragment.TaskFragment;
import br.com.gilbertopapa.android.model.Cidade;
import br.com.gilbertopapa.android.model.Temperatura;
import br.com.gilbertopapa.android.utils.ConnectivityUtils;
import br.com.gilbertopapa.android.utils.Constants;
import br.com.gilbertopapa.android.webservice.WebServiceProxy;

public class MainActivity extends Activity implements TaskFragment.TaskListener<List<Cidade>> {

	// Adapter para exibição das cidades na lista
	private CidadesAdapter adapter;
	
	// Task para exibição de cidades
	private ListCidadesTask task;
	
	// Flag que indica se a lista de cidades deve ser atualizada
	private boolean refresh;
	
	// Flag que indica se a lista de cidades está em processo de atualização
	private boolean inProgress;

    private ListView listView;

    private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progress);

		// Define o adapter para a lista
		adapter = new CidadesAdapter();
		listView.setAdapter(adapter);

		// Cria a task
		task = TaskFragment.getInstance(this, ListCidadesTask.class);

		if (savedInstanceState == null) {
			// Se a activity está sendo iniciada pela primeira vez, marca a flag de refresh para true
			refresh = true;

		} else {
			// Se a activity está sendo recriada após uma mudança de configuração, obtém a lista de cidades que foi salva
			// previamente e carrega o adapter
			CidadesHolder holder = (CidadesHolder) savedInstanceState.getSerializable("cidades");
			adapter.setCidades(holder.getCidades());

			// Se a lista estava em processo de atualização quando a configuração mudou, exibe o indicador de progresso
			inProgress = savedInstanceState.getBoolean("inProgress");
			if (inProgress) {
				setInProgress(true);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Se o refresh deve ser feito, inicia o refresh
		if (refresh) {
			refresh();
			refresh = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			// Faz o refresh da lista
			refresh();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Coloca a lista de cidades no Bundle para recuperação posterior. Encapsula a lista dentro de um
		// CidadesHolder para que seja possível a inserção no Bundle
		outState.putSerializable("cidades", new CidadesHolder(adapter.getCidades()));
		
		outState.putBoolean("inProgress", inProgress);
	}

	private void refresh() {
		if (!ConnectivityUtils.isConnected(this)) {
			// Se não existe conexão de dados, mostra o dialog de erro e termina
			ConnErrorDialog.show(getFragmentManager());

		} else {
			// Executa a task de refresh
			task.execute(0);
		}
	}

    private void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;

        if (inProgress) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

	// Método chamado na UI thread logo antes da task começar
	@Override
	public void beforeTaskExecute(int taskId) {
		// Exibe o indicador de progresso e remove os elementos da lista

		setInProgress(true);
		adapter.clear();
	}

	// Método chamado na UI thread assim que a task termina
	@Override
	public void afterTaskExecute(int taskId, List<Cidade> cidades) {
		// Coloca os elementos lidos na lista e esconde o indicador de progresso
		adapter.setCidades(cidades);
		setInProgress(false);
	}

	public static class ListCidadesTask extends TaskFragment<Void, List<Cidade>> {
		// Método da task. Executa em background.
		@Override
		public List<Cidade> executeInBackground(int taskId) {
			try {
				// Cria o proxy de acesso ao web service
				WebServiceProxy proxy = new WebServiceProxy();
				
				// Obtém a lista de cidades
				List<Cidade> cidades = proxy.listCidades();
				
				// Para cada cidade, obtém a temperatura com base no ID da cidade
				for (Cidade cidade : cidades) {
					Temperatura temperatura = proxy.obterTemperatura(cidade.getId());
					cidade.setTemperatura(temperatura);
				}

                // Sleep para atrasar o carregamento. Usado apenas para fins de testes
                SystemClock.sleep(3000);

				return cidades;

			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Erro ao executar a task", e);
				return Collections.emptyList();
			}
		}
	}

	// Classe que encapsula uma lista de cidades. Necessária para salvar as cidades em um Bundle
	public static class CidadesHolder implements Serializable {
		private List<Cidade> cidades;

		public CidadesHolder(List<Cidade> cidades) {
			this.cidades = cidades;
		}

		public List<Cidade> getCidades() {
			return cidades;
		}
	}
}

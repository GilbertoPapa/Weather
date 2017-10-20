package br.com.gilbertopapa.android.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

// Fragment para execução de uma task em background
// Params: tipo dos parâmetros enviados à task
// Result: tipo de dados retornado pela task
@SuppressWarnings("unchecked")
public abstract class TaskFragment<Params, Result> extends Fragment {

	public static final String TAG = "TaskFragment";

	// Flag que indica se a activity está pronta para ser chamada
	private boolean activityReady;
	
	// Lista de execuções pendentes a serem enviadas à activity
	private List<Runnable> pendingCallbacks = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Define que o fragment não será destruído quando a activity for destruída. Isto permite que o fragment
		// continue executando a task em background mesmo quando ocorre uma mudança de configuração
		setRetainInstance(true);
	}

	// O Android chama este método quando o onCreate() da activity terminou de executar. A activity pode ser
	// acessada a partir do fragment;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (pendingCallbacks.size() > 0) {
			// Se existem execuções pendentes, envia para a activity
			for (Runnable callback : pendingCallbacks) {
				getActivity().runOnUiThread(callback);
			}
			
			// Limpa a lista de execuções pendentes
			pendingCallbacks.clear();
		}

		// Define a flag de activity pronta como true
		activityReady = true;
	}

	// O Android chama este método quando a activity é desligada do fragment
	@Override
	public void onDetach() {
		super.onDetach();
		
		// Define a flag de activity pronta como false
		activityReady = false;
	}

	// Retorna a activity como um TaskListener. Se a activity não implementar esta interface, retorna null
	private TaskListener<Result> getTaskActivity() {
		Activity activity = getActivity();
		
		if (activity instanceof TaskListener) {
			return (TaskListener<Result>) activity;
		
		} else {
			return null;
		}
	}

	// Executa a task. A task recebe um ID e um varargs de parâmetros. Internamente, usa uma AsyncTask para fazer o serviço.
	public void execute(int taskId, Params... params) {
		BackgroundTask task = new BackgroundTask(taskId);
		task.execute(params);
	}

	// Executa a task. A task recebe um ID.
	public void execute(int taskId) {
		execute(taskId, (Params[]) null);
	}

	// Método de execução da task, que será executada em background. Deve ser implementado pelas subclasses.
	protected abstract Result executeInBackground(int taskId);

	// Classe de AsyncTask responsável pela execução em background
	private class BackgroundTask extends AsyncTask<Params, Void, Result> {

		// ID da task
		private int taskId;

		public BackgroundTask(int taskId) {
			this.taskId = taskId;
		}

		@Override
		protected void onPreExecute() {
			// Avisa a activity de que a task está prestes a começar
			TaskListener<Result> activity = getTaskActivity();
			if (activity != null) {
				activity.beforeTaskExecute(taskId);
			}
		}

		@SafeVarargs
        @Override
		protected final Result doInBackground(Params... params) {
			return executeInBackground(taskId);
		}
		
		// Avisa a activity que a task terminou. Se a activity não estiver disponível, coloca a execução
		// na fila de execuções pendentes, para que ela seja executada mais tarde, quando a activity estiver pronta
		@Override
		protected void onPostExecute(final Result result) {
			final TaskListener<Result> activity = getTaskActivity();

			if (activity != null) {
				if (activityReady) {
					activity.afterTaskExecute(taskId, result);

				} else {
					pendingCallbacks.add(new Runnable() {
						public void run() {
							activity.afterTaskExecute(taskId, result);
						}
					});
				}
			}
		}
	}

	// Listener para task. Pode ser implementado pela activity que vai chamar a task
	public interface TaskListener<Result> {
		public void beforeTaskExecute(int taskId);
		public void afterTaskExecute(int taskId, Result result);
	}

	// Obtém uma instância de TaskFragment
	public static <Params, Result, F extends TaskFragment<Params, Result>> F getInstance(Activity activity, Class<? extends TaskFragment<Params, Result>> taskFragmentClass) {
		
		// Verifica se o fragment já está atrelado à activity
		TaskFragment<Params, Result> taskFragment = (TaskFragment<Params, Result>) activity.getFragmentManager().findFragmentByTag(TAG);

		if (taskFragment == null) {
			// Se não estiver, cria uma instância do fragment e adiciona à activity
			try {
				taskFragment = taskFragmentClass.newInstance();
				activity.getFragmentManager().beginTransaction().add(taskFragment, TAG).commit();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return (F) taskFragment;
	}
}

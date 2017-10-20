package br.com.gilbertopapa.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.gilbertopapa.android.R;
import br.com.gilbertopapa.android.model.Cidade;

public class CidadesAdapter extends BaseAdapter {

	// Lista que armazena as cidades carregadas
	private List<Cidade> cidades = new ArrayList<>();

	// Retorna a quantidade de elementos na lista
	@Override
	public int getCount() {
		return cidades.size();
	}

	// Retorna a cidade de uma posição específica
	@Override
	public Object getItem(int position) {
		return cidades.get(position);
	}

	// Retorna o ID de uma posição específica
	@Override
	public long getItemId(int position) {
		return cidades.get(position).getId();
	}

	// Constrói as views que representam cada uma das linhas da lista
	// Implementa o padrão View Holder para obter melhor performance
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;

		if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.adapter_cidades, parent, false);
			holder = new ViewHolder();
			holder.txtCidade = (TextView) view.findViewById(R.id.txt_cidade);
			holder.txtPais = (TextView) view.findViewById(R.id.txt_pais);
			holder.txtMin = (TextView) view.findViewById(R.id.txt_min);
			holder.txtMax = (TextView) view.findViewById(R.id.txt_max);
			view.setTag(holder);

        } else {
			holder = (ViewHolder) view.getTag();
		}

		Cidade cidade = cidades.get(position);
		holder.txtCidade.setText(cidade.getNome());
		holder.txtPais.setText(cidade.getPais());
		holder.txtMin.setText("min: " + cidade.getTemperatura().getMin());
		holder.txtMax.setText("máx: " + cidade.getTemperatura().getMax());
		
		return view;
	}

	// Define a lista de cidades
	public void setCidades(List<Cidade> cidades) {
		this.cidades.clear();
		this.cidades.addAll(cidades);
		Collections.sort(this.cidades);
		notifyDataSetChanged();
	}
	
	// Retorna a lista de cidades
	public List<Cidade> getCidades() {
		return cidades;
	}

	// Remove todos os elementos da lista
	public void clear() {
		this.cidades.clear();
		notifyDataSetChanged();
	}

	// Classe para implementação do padrão View Holder
	private static class ViewHolder {
		public TextView txtCidade;
		public TextView txtPais;
		public TextView txtMin;
		public TextView txtMax;
	}
}

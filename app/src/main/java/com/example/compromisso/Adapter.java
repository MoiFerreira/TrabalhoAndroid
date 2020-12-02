package com.example.compromisso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Compromisso> items;

    public Adapter(){

    }

    public Adapter(List<Compromisso> itens){
        this.items = itens;
    }

    // Método para que possamos criar as primeiras views(porém, não exibe os itens das views).
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Convertendo o nosso xml base para Recyclerview, em uma view.
        // parent.getContext() recupera o contexto baseado no componente que o nosso itemLista
        // está dentro.
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list, parent, false);
        return new MyViewHolder(itemLista);
    }

    // Método que reaproveita as views e troca apenas o objeto de dentro dela.
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Mostrando/setando os dados dos itens das views.
        Compromisso item = items.get(position);
        String formated = null;
        try {
            Date dateTimePattern = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00").parse(item.getData());
            formated = new SimpleDateFormat("E, dd 'de' MMM, 'de' yyyy 'às' HH:mm").format(dateTimePattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txtTitulo.setText(item.getTitulo());
        holder.txtDescricao.setText(item.getDescricao());
        holder.txtData.setText(formated);
    }

    // Retorna a quantidade de itens a serem exibidos.
    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtData, txtDescricao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Localizando os componentes da view.
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtData = itemView.findViewById(R.id.txtData);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
        }
    }
}

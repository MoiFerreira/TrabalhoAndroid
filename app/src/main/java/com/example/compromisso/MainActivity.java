package com.example.compromisso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private String userId;
    private Context mContext;
    private Intent intent;
    private List<Compromisso> compromissos = new ArrayList<Compromisso>();

    private TextView textWarning;
    private RecyclerView recyclerList;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onResume() {
        super.onResume();
        requisicaoDeCompromissos();
    }

    public String getUserId() {
        SharedPreferences sharedPref = this.getPreferences(mContext.MODE_PRIVATE);
        String id = sharedPref.getString("userId", null);
        if (id == null) {
            id = String.valueOf(UUID.randomUUID());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userId", id);
            editor.commit();
        }
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        intent = getIntent();
        userId = getUserId();

        textWarning = findViewById(R.id.textWarning);
        recyclerList = findViewById(R.id.recyclerList);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerList.setHasFixedSize(true);
        recyclerList.setLayoutManager(linearLayoutManager);
        recyclerList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.VERTICAL));

        recyclerList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        mContext,
                        recyclerList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Compromisso compromisso = compromissos.get(position);
                                abrirDialog(compromisso);
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Create.class);
                intent.putExtra("userId", userId);
                //finish();
                startActivity(intent);
            }
        });
        textWarning.setText("Procurando compromissos...");
    }



    public void requisicaoDeCompromissos(){
        String url = "https://andrade-api.herokuapp.com/compromissos/" + userId;
        JsonArrayRequest requisicao = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override//
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() == 0) throw new JSONException("Lista Vazia");
                            compromissos.clear();
                            for(int i=0; i<response.length(); i++) {
                                JSONObject post = response.getJSONObject(i);
                                String titulo = post.getString("titulo");
                                String descricao = post.getString("descricao");
                                String data = post.getString("data");
                                int id = post.getInt("id");
                                compromissos.add(new Compromisso(titulo, descricao, data, id));
                            }
                            Adapter adapter = new Adapter(compromissos);
                            recyclerList.setAdapter(adapter);
                            textWarning.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Adapter adapter = new Adapter(new ArrayList<>());
                            recyclerList.setAdapter(adapter);
                            textWarning.setText("Nenhum compromisso marcado");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  mTextView.setText("deu ruim" + error.toString());
                    }});
        Volley.newRequestQueue(mContext).add(requisicao);
    }


    private void apagarPost(int id) {
        String url = String.format("https://andrade-api.herokuapp.com/compromissos/%s/%d", userId, id);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        toastAlert("Compromisso Apagado!");
                        requisicaoDeCompromissos();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toastAlert("Ocorreu um erro");
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }
    private void editarPost(Compromisso compromisso) {
        intent = new Intent(MainActivity.this, Edit.class);
        intent.putExtra("id", compromisso.getId());
        intent.putExtra("userId", userId);
        intent.putExtra("titulo", compromisso.getTitulo());
        intent.putExtra("descricao", compromisso.getDescricao());
        intent.putExtra("data", compromisso.getData());
        startActivity(intent);
    }


    private void toastAlert(String message) {
        Toast.makeText(
                mContext,
                message,
                Toast.LENGTH_LONG
        ).show();
    }



    public void abrirDialog(Compromisso compromisso) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ações");
        String[] opcoes = {"Apagar", "Editar"};
        dialog.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if (opcoes[which] == "Apagar") {
                    apagarPost(compromisso.getId());
                } else if (opcoes[which] == "Editar") {
                    editarPost(compromisso);
                }
            }
        });
        dialog.show();
    }



}
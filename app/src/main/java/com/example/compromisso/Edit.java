package com.example.compromisso;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Edit extends AppCompatActivity {

    private Context mContext;
    private String userId;
    private TextInputLayout mTitulo;
    private TextInputLayout mDescricao;
    private TextInputLayout mData;
    private Button botaoSalvar;
    private Intent intent;
    private Button inserirData;
    private int postId;
    private String simpleDataFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mContext = getApplicationContext();
        intent = getIntent();
        postId = intent.getIntExtra("id", 0);
        userId = intent.getStringExtra("userId");
        mTitulo = findViewById(R.id.tituloText);
        mDescricao = findViewById(R.id.descricaoText);
        mData = findViewById(R.id.dataText);
        botaoSalvar = findViewById(R.id.buttonSalvar);
        inserirData = findViewById(R.id.inserirData);

        if (postId == 0) finish();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        simpleDataFormat = intent.getStringExtra("data");

        mTitulo.getEditText().setText(intent.getStringExtra("titulo"));
        mDescricao.getEditText().setText(intent.getStringExtra("descricao"));
        try {
            final Date parsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00").parse(simpleDataFormat);
            final String formated = new SimpleDateFormat("E, dd 'de' MMM, 'de' yyyy 'às' HH:mm").format(parsed);
            mData.getEditText().setText(formated);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePost(
                        mTitulo.getEditText().getText().toString(),
                        mDescricao.getEditText().getText().toString(),
                        simpleDataFormat,
                        postId
                );
            }
        });

        inserirData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int ano = calendar.get(Calendar.YEAR);

                DatePickerDialog dataPickerDialog = new DatePickerDialog(Edit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mAno, int mMes, int mDia) {
                        int hora = calendar.get(Calendar.HOUR);
                        int minuto = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int mHora, int mMinuto) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                                SimpleDateFormat dateTimePattern = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00");
                                String datetime = String.format("%d/%d/%d %s:%s:%s", mDia, mMes, mAno, String.format("%02d", mHora), String.format("%02d", mMinuto), "00");
                                Date parsedDatetime = null;
                                String formated = null;
                                try {
                                    parsedDatetime = dateFormat.parse(datetime);
                                    formated = new SimpleDateFormat("E, dd 'de' MMM, 'de' yyyy 'às' HH:mm").format(parsedDatetime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                simpleDataFormat = dateTimePattern.format(parsedDatetime);
                                mData.getEditText().setText(formated);
                            }
                        }, hora, minuto, DateFormat.is24HourFormat(mContext));
                        timePickerDialog.show();
                    }
                }, ano, mes, dia);
                dataPickerDialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updatePost(String titulo, String descricao, String data, int id) {
        String url = String.format("https://andrade-api.herokuapp.com/compromissos/%s/%d", userId, id);

        Map<String, String> body = new HashMap<>();
        body.put("titulo", titulo);
        body.put("descricao", descricao);
        body.put("data", data);

        JSONObject bodyJSON = new JSONObject(body);

        JsonObjectRequest requisicao = new JsonObjectRequest
                (Request.Method.PUT, url, bodyJSON, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                mContext,
                                "Compromisso editado",
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                mContext,
                                "Erro ao tentar editar compromisso",
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                    }
                });
        Volley.newRequestQueue(mContext).add(requisicao);
    }

}

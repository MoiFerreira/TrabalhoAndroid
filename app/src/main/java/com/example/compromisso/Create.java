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
import android.widget.LinearLayout;
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

public class Create extends AppCompatActivity {

    private Context mContext;
    private String userId;
    private TextInputLayout mTitulo;
    private TextInputLayout mDescricao;
    private TextInputLayout mData;
    private String simpleDataFormat;
    private Button botaoCadastrar;
    private Button inserirData;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mContext = getApplicationContext();
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        mTitulo = findViewById(R.id.tituloText);
        mDescricao = findViewById(R.id.descricaoText);
        mData = findViewById(R.id.dataText);
        botaoCadastrar = findViewById(R.id.buttonSalvar);
        inserirData = findViewById(R.id.inserirData);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPost(
                        mTitulo.getEditText().getText().toString(),
                        mDescricao.getEditText().getText().toString(),
                        simpleDataFormat
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

                DatePickerDialog dataPickerDialog = new DatePickerDialog(Create.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mAno, int mMes, int mDia) {
                        int hora = calendar.get(Calendar.HOUR);
                        int minuto = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Create.this, new TimePickerDialog.OnTimeSetListener() {
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

    public void insertPost(String titulo, String descricao, String data) {
        String url = "https://andrade-api.herokuapp.com/compromissos/" + userId;
        Map<String, String> params = new HashMap();
        params.put("titulo", titulo);
        params.put("descricao", descricao);
        params.put("data", data);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                mContext,
                                "Postado com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                mContext,
                                "Houve um erro durante a tentativa de inserir o compromisso",
                                Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(mContext).add(request);
    }


}

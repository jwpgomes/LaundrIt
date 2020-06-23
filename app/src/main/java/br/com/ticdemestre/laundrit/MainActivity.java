package br.com.ticdemestre.laundrit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String lang = "pt";
    String mail = "";
    String senha = "";
    String resposta = "";
    String versao = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lerIdioma();

        setContentView(R.layout.activity_main);

        carregarImagemIdioma();

        SharedPreferences dados = getSharedPreferences("laundrit", MODE_PRIVATE);
        Integer conectado = dados.getInt("login", 0);
        mail = dados.getString("mail", "");
        senha = dados.getString("senha", "");

        if (conectado == 0)
        {
            Intent telaLogin = new Intent(this, LoginActivity.class);
            startActivityForResult(telaLogin, 2);
        }
        else
        {
            postRequest();
        }
    }

    public void lerIdioma() {
        SharedPreferences dados = getSharedPreferences("laundrit", MODE_PRIVATE);
        lang = dados.getString("idioma", "pt");

        Locale idioma = new Locale(lang);
        Locale.setDefault(idioma);

        Context context = this;
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(idioma);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public void carregarImagemIdioma() {
        ImageView imagem = (ImageView) findViewById(R.id.imgIdioma);

        String nomeImagem = "@drawable/" + lang;
        int imageResource = getResources().getIdentifier(nomeImagem, null, getPackageName());

        Drawable res = ContextCompat.getDrawable(getApplicationContext(), imageResource);

        imagem.setImageDrawable(res);
    }

    public void telaIdioma(View v) {
        Intent tela = new Intent(this, IdiomaActivity.class);
        startActivityForResult(tela, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            recreate();
        }
    }

    public void sair(View v) {

        SharedPreferences.Editor dados = getSharedPreferences("laundrit", MODE_PRIVATE).edit();
        dados.putInt("login", 0);
        dados.commit();

        recreate();
    }

    private void postRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://ticdemestre.com.br/laundrit/post_login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    resposta = jsonObject.getString("resultado");
                    versao = jsonObject.getString("versao");

                    if (resposta.contains("OK")) {
                        Toast.makeText(MainActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        SharedPreferences.Editor dados = getSharedPreferences("laundrit", MODE_PRIVATE).edit();
                        dados.putInt("login", 0);
                        dados.commit();
                        Toast.makeText(MainActivity.this, "Erro no Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resposta = "POST DATA : unable to Parse Json";
                    Toast.makeText(MainActivity.this, "POST DATA : unable to Parse Json", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resposta = "Post Data : Response Failed";
                Toast.makeText(MainActivity.this, "Post Data : Response Failed" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("data_mail", mail);
                params.put("data_senha", senha);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}

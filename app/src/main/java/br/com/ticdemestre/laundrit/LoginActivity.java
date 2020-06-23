package br.com.ticdemestre.laundrit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    String resposta = "";
    String versao = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void entrar(View v) {
        EditText mail = (EditText) findViewById(R.id.edtMail);
        EditText senha = (EditText) findViewById(R.id.edtSenha);

        SharedPreferences.Editor dados = getSharedPreferences("laundrit", MODE_PRIVATE).edit();
        dados.putString("mail", mail.getText().toString());
        dados.putString("senha", senha.getText().toString());
        // dados.putInt("login", 1);
        dados.commit();

        // finish();

        postRequest();
    }

    public void cadastro(View v) {
        // Intent tela = new Intent(this, CadastroActivity.class);
        // startActivity(tela);
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
                        Toast.makeText(LoginActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor dados = getSharedPreferences("laundrit", MODE_PRIVATE).edit();
                        dados.putInt("login", 1);
                        dados.putInt("versao", Integer.parseInt(versao) );
                        dados.commit();

                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro no Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resposta = "POST DATA : unable to Parse Json";
                    Toast.makeText(LoginActivity.this, "POST DATA : unable to Parse Json", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resposta = "Post Data : Response Failed";
                Toast.makeText(LoginActivity.this, "Post Data : Response Failed" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                EditText mail = (EditText) findViewById(R.id.edtMail);
                EditText senha = (EditText) findViewById(R.id.edtSenha);

                Map<String, String> params = new HashMap<String, String>();
                params.put("data_mail", mail.getText().toString());
                params.put("data_senha", senha.getText().toString());
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

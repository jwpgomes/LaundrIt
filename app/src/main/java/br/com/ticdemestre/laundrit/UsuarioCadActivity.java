package br.com.ticdemestre.laundrit;

import androidx.appcompat.app.AppCompatActivity;

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

public class UsuarioCadActivity extends AppCompatActivity {
    String resposta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_cad);

        EditText mail = (EditText) findViewById(R.id.edtMail);
        EditText nome = (EditText) findViewById(R.id.edtNome);

        SharedPreferences dados = getSharedPreferences("laundrit", MODE_PRIVATE);
        mail.setText(dados.getString("mail", " "));
        nome.setText(dados.getString("nome", " "));
    }

    public void salvar(View v) {
        EditText mail = (EditText) findViewById(R.id.edtMail);
        EditText senha = (EditText) findViewById(R.id.edtSenha);
        EditText nome = (EditText) findViewById(R.id.edtNome);

        SharedPreferences.Editor dados = getSharedPreferences("laundrit", MODE_PRIVATE).edit();
        dados.putString("mail", mail.getText().toString());
        dados.putString("senha", senha.getText().toString());
        dados.putString("nome", nome.getText().toString());
        dados.putInt("login", 0);
        dados.commit();

        postRequest();
    }

    public void fechar(View v) {
        finish();
    }

    private void postRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://ticdemestre.com.br/laundrit/post_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    resposta = jsonObject.getString("resultado");

                    if (resposta.contains("OK")) {
                        Toast.makeText(UsuarioCadActivity.this, "Cadastro efetuado com sucesso", Toast.LENGTH_LONG).show();

                        finish();
                    } else if (resposta.contains("EXISTE")) {
                        Toast.makeText(UsuarioCadActivity.this, "e-mail já cadastrado no sistema", Toast.LENGTH_LONG).show();
                    } else if (resposta.contains("BD ERRO")) {
                        Toast.makeText(UsuarioCadActivity.this, "Erro na inserção no Banco de Dados", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UsuarioCadActivity.this, "Erro no Cadastro", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resposta = "POST DATA : unable to Parse Json";
                    Toast.makeText(UsuarioCadActivity.this, "POST DATA : unable to Parse Json", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resposta = "Post Data : Response Failed";
                Toast.makeText(UsuarioCadActivity.this, "Post Data : Response Failed" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                EditText mail = (EditText) findViewById(R.id.edtMail);
                EditText senha = (EditText) findViewById(R.id.edtSenha);
                EditText nome = (EditText) findViewById(R.id.edtNome);

                Map<String, String> params = new HashMap<String, String>();
                params.put("data_mail", mail.getText().toString());
                params.put("data_senha", senha.getText().toString());
                params.put("data_nome", nome.getText().toString());
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

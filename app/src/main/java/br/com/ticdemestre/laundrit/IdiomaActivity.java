package br.com.ticdemestre.laundrit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

public class IdiomaActivity extends AppCompatActivity {
    String lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lerIdioma();

        setContentView(R.layout.activity_idioma);
    }

    public void mudarIdioma(View v) {
        ImageView opcao = (ImageView) findViewById(v.getId());
        String op = opcao.getContentDescription().toString();

        SharedPreferences.Editor dados = getSharedPreferences("boraComprarTICdeMestre", MODE_PRIVATE).edit();
        dados.putString("idioma", op);
        dados.commit();

        recreate();
    }

    public void lerIdioma() {
        SharedPreferences dados = getSharedPreferences("boraComprarTICdeMestre", MODE_PRIVATE);
        lang = dados.getString("idioma", "pt");

        Locale idioma = new Locale(lang);
        Locale.setDefault(idioma);

        Context context = this;
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(idioma);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public void fechar(View v) {
        finish();
    }
}

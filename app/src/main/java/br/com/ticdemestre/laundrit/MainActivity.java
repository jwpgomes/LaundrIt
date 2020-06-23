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

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String lang = "pt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lerIdioma();

        setContentView(R.layout.activity_main);

        carregarImagemIdioma();
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
}

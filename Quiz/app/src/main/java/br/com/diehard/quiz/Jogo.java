package br.com.diehard.quiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Jogo extends AppCompatActivity {

    private int idEvento;
    private int idGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
        //TODO:colocar o grupo nosingleton
        idGrupo = ps.codGrupo;

        //get id do evento
        Intent intent = getIntent();
        idEvento = intent.getIntExtra("idEvento", 0);
        String tipoQuestao = intent.getStringExtra("tipoQuestao");


        if(savedInstanceState == null )
        {
            if(tipoQuestao.equalsIgnoreCase("T")) {
                TextoFragment fragment = new TextoFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.jogo_container, fragment).commit();
            }
            else if(tipoQuestao.equalsIgnoreCase("A"))
            {
                AlternativaFragment fragment1 = new AlternativaFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.jogo_container, fragment1).commit();
            }
            else if(tipoQuestao.equalsIgnoreCase("V"))
            {
                VerdadeiroFragment fragment2 = new VerdadeiroFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.jogo_container, fragment2).commit();
            }
        }
    }

    //Servico de buscar questao ativa
    public class NetworkQuestao extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground (Void... params)
        {
            URL url = null;
            String result = "";

            try {
                //TODO: colocar o caminho certo do servidor
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/questao/"+idEvento+"/"+idGrupo);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                result = responseStrBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)
        {
            boolean statusQuestao = false;

            //TODO:apagar essa linha depois
            //result = "{\"codTipoQuestao\":\"A\",\"codQuestao\":null,\"textoQuestao\":null,\"alternativas\":null,\"tempo\":null,\"codAssunto\":null}";
            //result = "{\"codTipoQuestao\":null,\"codQuestao\":null,\"textoQuestao\":null,\"alternativas\":null,\"tempo\":null,\"codAssunto\":null}";
            try {
                JSONObject json = new JSONObject(result);

                if(!json.getString("codTipoQuestao").equals("null")){

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
            }
        }
    }

    //sobreescrita para inutilizar o botao voltar
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}

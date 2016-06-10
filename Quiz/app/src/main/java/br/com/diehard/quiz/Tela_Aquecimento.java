package br.com.diehard.quiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tela_Aquecimento extends AppCompatActivity {

    private int idEvento;
    private int idGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela__aquecimento);

        ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
        //TODO:colocar o grupo nosingleton
        idGrupo = ps.codGrupo;

        //get id do evento
        Intent intent = getIntent();
        idEvento = intent.getIntExtra("idEvento", 0);

        //SERVICES
        Network e = new Network();
        e.execute((Void)null);
    }

    //SERVICO
    public class Network extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground (Void... params)
        {
            URL url = null;
            String result = "";

            try {
                //TODO: colocar o caminho certo do servidor
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/jogo/"+idEvento);

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
            //TODO:apagar essa linha depois
            result = "{\"status\":\"E\"}";
            try {

                JSONObject json = new JSONObject(result);
                String statusEvento = json.getString("status");

                if(statusEvento.equals("E"))
                {
                    //TODO:ver questao em aberto questaoStatus = A
                    new NetworkQuestao().execute((Void)null);
                }
                else if(statusEvento.equals("F"))
                {
                    Intent i = new Intent(Tela_Aquecimento.this, ResultadoActivity.class);
                    i.putExtra("idEvento", idEvento);
                    startActivity(i);
                }
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
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
            result = "{\"codTipoQuestao\":\"T\",\"codQuestao\":null,\"textoQuestao\":null,\"alternativas\":null,\"tempo\":null,\"codAssunto\":null}";
            //result = "{\"codTipoQuestao\":null,\"codQuestao\":null,\"textoQuestao\":null,\"alternativas\":null,\"tempo\":null,\"codAssunto\":null}";
            try {
                JSONObject json = new JSONObject(result);

                if(!json.getString("codTipoQuestao").equals("null"))
                {
                    //TODO:Deletear o jogo.java
                    if(json.getString("codTipoQuestao").equalsIgnoreCase("T")) {
                        Intent i = new Intent(Tela_Aquecimento.this, JogoTexto.class);
                        i.putExtra("idEvento", idEvento);
                        startActivity(i);
                    }
                    else if(json.getString("codTipoQuestao").equalsIgnoreCase("A"))
                    {
                        Intent i = new Intent(Tela_Aquecimento.this, JogoTexto.class);
                        i.putExtra("idEvento", idEvento);
                        startActivity(i);
                    }
                    else if(json.getString("codTipoQuestao").equalsIgnoreCase("V"))
                    {
                        Intent i = new Intent(Tela_Aquecimento.this, JogoTexto.class);
                        i.putExtra("idEvento", idEvento);
                        startActivity(i);
                    }
                    statusQuestao = true;
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
            }

            //caso de erro ou result seja vazio chama de novo a thread de status de evento
            if(!statusQuestao)
            {
                Network e = new Network();
                e.execute((Void) null);
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

package br.com.diehard.quiz;

import android.app.ProgressDialog;
import android.content.Context;
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

public class ResultadoActivity extends AppCompatActivity {

    private ViewGroup group;
    private Context context;
    private TextView campeao;
    private ProgressDialog progress;

    private int idEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        context = this;
        campeao = (TextView) findViewById(R.id.vencedor);

        ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
        idEvento = ps.codEvento;

        //SERVICES
        Network e = new Network();
        e.execute((Void)null);
    }

    //SERVICO
    public class Network extends AsyncTask<Void, Void, String>
    {
        protected void onPreExecute(){
            progress = ProgressDialog.show(context, "", "Calculando os pontos dos grupos...", true);
        }

        protected String doInBackground (Void... param)
        {
            URL url = null;
            String result = "";

            try {
                //TODO: colocar o caminho certo do servidor
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/resultado/"+idEvento);

                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStream in = con.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while((inputStr = streamReader.readLine()) != null )
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
            //result = "[{\"nmGrupo\":\"grupo 1\",\"pontosGrupo\":2,\"pontosTotal\":13},{\"nmGrupo\":\"grupo 2\",\"pontosGrupo\":11,\"pontosTotal\":13},{\"nmGrupo\":\"yo\",\"pontosGrupo\":0,\"pontosTotal\":13}]";
            try {
                JSONArray json = new JSONArray(result);
                int vencedor = 0;
                //while para carregar o resultado
                group = (ViewGroup) findViewById(R.id.container);

                for(int i = 0; json.length() > i ;i++) {
                    JSONObject itemJson = json.getJSONObject(i);

                    LinearLayout item = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.resultadoitem, group, false);
                    TextView nomeGrupo = (TextView) item.findViewById(R.id.nome_grupo);
                    TextView pontos = (TextView) item.findViewById(R.id.pontos);
                    nomeGrupo.setText(itemJson.getString("nmGrupo"));
                    //pontos.setText(itemJson.getString("pontosGrupo") +"/" +itemJson.getString("pontosTotal"));
                    pontos.setText(itemJson.getString("pontosGrupo") +" pontos");
                    group.addView(item);

                    //texto para o vencedor
                    //TODO:no servico ja trazer o vencedor
                    if(itemJson.getInt("pontosGrupo") > vencedor)
                    {
                        vencedor = itemJson.getInt("pontosGrupo");
                        campeao.setText(itemJson.getString("nmGrupo"));
                    }
                }
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
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

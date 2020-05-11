package ab.appdev.study.covidstat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  public Button B;
  private TextView cci, ccf, dt, dis, ttl;
  private EditText E;
  final String myURL = "https://api.rootnet.in/covid19-in/stats/latest";
  JSONObject response = new JSONObject();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    E = (EditText) findViewById(R.id.editText);
    B = (Button) findViewById(R.id.button);
    ccf = (TextView) findViewById(R.id.ccf);
    cci = (TextView) findViewById(R.id.cci);
    ttl = (TextView) findViewById(R.id.ttl);
    dis = (TextView) findViewById(R.id.disch);
    dt = (TextView) findViewById(R.id.death);

    dataMGMT("Maharashtra");


    B.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dataMGMT(E.getText().toString());
          }
        });



  }

  void dataMGMT(final String State) {
    JsonObjectRequest jsonObjectRequest =
        new JsonObjectRequest(
            Request.Method.GET,
            myURL,
            response,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {

                try {

                  String data = response.getString("data");

                  JSONObject dataobj = new JSONObject(data);

                  String state = dataobj.getString("regional");

                  JSONArray ar = new JSONArray(state);

                  JSONObject pa;
                  boolean isfound = false;
                  for (int iter = 0; iter < ar.length(); iter++) {
                    pa = ar.getJSONObject(iter);

                    if (pa.getString("loc").contains(State)) {
                      ttl.setText(pa.getString("totalConfirmed"));
                      dis.setText(pa.getString("discharged"));
                      dt.setText(pa.getString("deaths"));
                      cci.setText(pa.getString("confirmedCasesIndian"));
                      ccf.setText(pa.getString("confirmedCasesForeign"));
                      isfound = true;
                      break;
                    }
                  }
                  if (!isfound)
                    Toast.makeText(getApplicationContext(), "not found ", Toast.LENGTH_SHORT)
                        .show();

                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

                B.setText(error.toString());
              }
            });
    MySingelton.getInstance(MainActivity.this).addToRequestQue(jsonObjectRequest);
  }
}

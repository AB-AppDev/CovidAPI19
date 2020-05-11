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

  public Button Search;
  private TextView indiancases, foreigncases, totaldeaths, dischargedcases, totalcases;
  private EditText StateInput;
  final String myURL = "https://api.rootnet.in/covid19-in/stats/latest";
  JSONObject response = new JSONObject();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    StateInput = (EditText) findViewById(R.id.statename);
    Search = (Button) findViewById(R.id.search);
    foreigncases = (TextView) findViewById(R.id.foreigncases);
    indiancases = (TextView) findViewById(R.id.indiancases);
    totalcases = (TextView) findViewById(R.id.totalcases);
    dischargedcases = (TextView) findViewById(R.id.disch);
    totaldeaths = (TextView) findViewById(R.id.death);

    dataMGMT("Maharashtra");

    Search.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dataMGMT(StateInput.getText().toString());
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
                      totalcases.setText(pa.getString("totalConfirmed"));
                      dischargedcases.setText(pa.getString("dischargedcasescharged"));
                      totaldeaths.setText(pa.getString("deaths"));
                      indiancases.setText(pa.getString("confirmedCasesIndian"));
                      foreigncases.setText(pa.getString("confirmedCasesForeign"));
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

                Search.setText(error.toString());
              }
            });
    MySingelton.getInstance(MainActivity.this).addToRequestQue(jsonObjectRequest);
  }
}

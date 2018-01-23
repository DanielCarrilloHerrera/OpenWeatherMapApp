package marwendoukh.openweathermapapp;


import android.widget.ProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Petición HTTP a esta URL, para recuperar condiciones del clima
    String weatherWebServiceURL = "http://api.openweathermap.org/data/2.5/weather?q=ariana,tn&appid=2156e2dd5b92590ab69c0ae1b2d24586&units=metric";

    //Dialogo de carga
    ProgressBar pBar;

    //Textview para mostrar temperatura y su descripción
    TextView temperature, description;

    //Imagen de fondo
    ImageView weatherBackground;

    //JSON Object que contiene la información del clima
    JSONObject jsonObject;

    //Layout de la pantalla
    RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = (TextView)findViewById(R.id.tvTemperature);
        description = (TextView) findViewById(R.id.tvDescription);
        weatherBackground = (ImageView) findViewById(R.id.weatherBackground);

        cargarBarraProgreso();
        realizarPeticionHttp();
    }

    public void cargarBarraProgreso(){
        pBar = new ProgressBar(this,null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(pBar, params);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(pBar,params);
        pBar.setVisibility(View.VISIBLE);  //Mostrar pBar
    }

    public void realizarPeticionHttp(){

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                weatherWebServiceURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Parsing json object response
                    // response will be a json object
                    jsonObject = (JSONObject) response.getJSONArray("weather").get(0);

                    //Mostrar la descripción el clima en el textview de descripción
                    description.setText(jsonObject.getString("description"));

                    //Mostrar la temperatura
                    temperature.setText(response.getJSONObject("main").getString("temp") + " °C");

                    //Imagen de fondo
                    String backgroundImage = "";

                    //Escoger la imagen para colocarla como background de acuerdo a la condición del clima
                    if (jsonObject.getString("main").equals("Clouds"))
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/clouds-wallpaper2.jpg";

                    else if (jsonObject.getString("main").equals("Rain"))
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/rainy-wallpaper1.jpg";

                    else if (jsonObject.getString("main").equals("Snow"))
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";

                    // Cargar imagen desde un link y mostrarlo en el fondo
                    //Se usa para esto Glide
                    Glide
                            .with(getApplicationContext())
                            .load(backgroundImage)
                            .centerCrop()
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {

                                @Override
                                public boolean onException(Exception e,
                                                           String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    System.out.println(e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource,
                                                               String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache,
                                                               boolean isFirstResource) {

                                    return false;
                                }
                            })
                            .into(weatherBackground);

                    //Esconder el dialogo de carga
                    pBar.setVisibility(View.INVISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error, try again !", Toast.LENGTH_LONG).show();
                    pBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error while loading ...", Toast.LENGTH_SHORT).show();
                //Ocultar la barra de progreso
                pBar.setVisibility(View.INVISIBLE);
            }
        });

        //Agregar petición a la cola de petición
        AppController.getInstance(this).addToRequestQueue(jsonObjRequest);

    }

}

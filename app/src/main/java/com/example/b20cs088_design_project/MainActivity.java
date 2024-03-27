package com.example.b20cs088_design_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
//
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
//    public static final MediaType JSON
//            = MediaType.get("application/json; charset=utf-8");
//    OkHttpClient client = new OkHttpClient();

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
//    private String url = "https://designprojectb20cs088.et.r.appspot.com/predict/";
    private String url = "https://designprojectb20cs088.et.r.appspot.com/predict/";

    private String[] happySongs = {
            // Hindi Songs
            "Happy Budday",
            "Badtameez Dil",
            "Balam Pichkari",
            "London Thumakda",
            "Navrai Majhi",
            "Jai Ho",
            "Mauja Hi Mauja",
            "Gallan Goodiyaan",
            "Kar Gayi Chull",
            "Chak De India",
            "Tum Hi Ho",
            "Ae Mere Humsafar",
            "Jeene Laga Hoon",
            "Radha",
            "Ainvayi Ainvayi",
            "Chaiyya Chaiyya",
            "Tera Ban Jaunga",
            "Dil Chahta Hai",
            "Jashn-e-Bahara",

            // English Songs
            "Happy" /* Pharrell Williams */,
            "Can't Stop the Feeling!" /* Justin Timberlake */,
            "Shut Up and Dance" /* WALK THE MOON */,
            "Uptown Funk" /* Mark Ronson ft. Bruno Mars */,
            "Don't Worry, Be Happy" /* Bobby McFerrin */,
            "I Gotta Feeling" /* The Black Eyed Peas */,
            "Happy Together" /* The Turtles */,
            "Walking on Sunshine" /* Katrina and the Waves */,
            "Best Day of My Life" /* American Authors */,
            "Good Vibrations" /* The Beach Boys */
    };

    private String[] sadSongs = {
            // Hindi Sad Songs
            "Tum Hi Ho",
            "Channa Mereya",
            "Jeene Laga Hoon",
            "Tere Bina",
            "Kabira",
            "Hasi Ban Gaye",
            "Agar Tum Saath Ho",
            "Tera Ban Jaunga",
            "Dil De Diya Hai",
            "Tujhe Bhula Diya",

            // English Sad Songs
            "Someone Like You" /* Adele */,
            "Yesterday" /* The Beatles */,
            "Hurt" /* Johnny Cash (Nine Inch Nails cover) */,
            "Nothing Compares 2 U" /* Sinead O'Connor (Prince cover) */,
            "Back to Black" /* Amy Winehouse */,
            "The Night We Met" /* Lord Huron */,
            "All I Want" /* Kodaline */,
            "Creep" /* Radiohead */,
            "Say Something" /* A Great Big World & Christina Aguilera */,
            "Everybody Hurts" /* R.E.M. */
    };
    private static <T> T getRandomItem(T[] array) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(array.length);
        return array[randomIndex];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
//            callAPI(question);
            postDataUsingVolley(question);
            welcomeTextView.setVisibility(View.GONE);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    private void postDataUsingVolley(String chat) {
        // url to post our data
        String url = "https://designprojectb20cs088.et.r.appspot.com/predict";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String uri = "https://designprojectb20cs088.et.r.appspot.com/predict?key=" + chat;
        messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));

        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE", response);
                        Double happy = Double.valueOf(response);
                        addResponse("Positive Mood probability= "+ response);
                        addToChat("Try this song", Message.SENT_BY_BOT);


                        if(happy > 0.5){
                            addToChat(getRandomItem(happySongs).toString(), Message.SENT_BY_BOT);
                        }
                        else{
                            addToChat(getRandomItem(sadSongs).toString(), Message.SENT_BY_BOT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Err", error.toString());
                    }
                });
        queue.add(myReq);
    }




}





















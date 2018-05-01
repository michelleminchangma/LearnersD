// ift 2905
// Minchang Ma
// Xiuli Zhang
// Wei Hu


package com.example.michellema.learnersd;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private Button btn_search;
    private TextView content;
    private DBHelper dbHelper;
    private Button btn_chercher;
    private Button btn_apprentissage;
    private String explanation = "";
    private String inputString;
    private Global get_global;
    private String user;
    private StringBuffer finalData;
    private String finalJson;
    private Button btn_wiki;
    private Button btn_image;
    private Button btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        input = (EditText) findViewById(R.id.et_input);
        btn_search = (Button) findViewById((R.id.btn_search));
        content = (TextView) findViewById(R.id.tv_content);
        btn_chercher = (Button) findViewById((R.id.btn_chercher));
        btn_apprentissage = (Button) findViewById((R.id.btn_apprentissage));
        btn_wiki = (Button) findViewById(R.id.btn_wiki);
        btn_image = (Button) findViewById(R.id.btn_image);
        btn_location = (Button) findViewById(R.id.btn_location);

        dbHelper = new DBHelper(MainActivity.this);
        dbHelper.openDB();
        get_global = Global.getInstance();
        user = get_global.getUser();

        // before search, put the list of the recently searched 10 vocabulary
        finalData = new StringBuffer();
        Cursor cursor = dbHelper.getAllRecords(user);

        int i = 0;
        cursor.moveToLast();
        while( i < 10 && !cursor.isBeforeFirst()){
            String voc = cursor.getString(cursor.getColumnIndex(DBHelper.VOCABULARY));
            if(!voc.equals("NO DATA")) {
                finalData.append(voc);
                finalData.append("\n");

            }
            cursor.moveToPrevious();
            i++;
        }

        content.setText(finalData);


        // dictionary from Github https://github.com/adambom/dictionary
        new JSONTask().execute("https://raw.githubusercontent.com/adambom/dictionary/master/dictionary.json");

        // during search
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputString = getValue(input);

                if(!inputString.equals("")) {
                    // search word from Json
                    explanation = "ERROR!!";
                    try {
                        JSONObject finalObject = new JSONObject(finalJson);
                        explanation = finalObject.getString(inputString.toUpperCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        explanation = "Sorry we can't find the word!";

                    }

                    finalData = new StringBuffer();
                    String[] tokens = explanation.split(";|\\.[^)]"); //[Obs.]
                    for(int i=0; i<tokens.length; i++){
                        finalData.append(tokens[i]);
                        finalData.append("\n\n");
                    }
                    content.setText(finalData);

                    // put the searched vocabulary to database
                    if(!explanation.equals("ERROR!!")) {
                        int id_DB = -1;
                        Cursor cursor = dbHelper.getAllRecords("default_user");
                        if (cursor.getCount() != 0) {
                            cursor.moveToLast();
                            id_DB = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));// if input the same word, put into database 2 times in different id
                        }
                        long resultInsert = dbHelper.insert(++id_DB, user, "NO DATA", "NO DATA", inputString, 3, explanation);// id should be increment auto
                        if (resultInsert == -1) {
                            Toast.makeText(getApplicationContext(), "Some errors occurred while inserting", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), "Data inserted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        btn_apprentissage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LearnActivity.class);
                startActivity(intent);
            }
        });

        btn_wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_global.setUrl("https://fr.wikipedia.org/wiki/" + getValue(input));
                Intent intent = new Intent(MainActivity.this, WebResourcesActivity.class);
                startActivity(intent);
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_global.setUrl("https://www.google.ca/search?q=" + getValue(input) + "&source=lnms&tbm=isch");
                Intent intent = new Intent(MainActivity.this, WebResourcesActivity.class);
                startActivity(intent);
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_global.setUrl("https://www.google.ca/maps/place/" + getValue(input));
                Intent intent = new Intent(MainActivity.this, WebResourcesActivity.class);
                startActivity(intent);
            }
        });

    }

    public String getValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    public class JSONTask extends AsyncTask <String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            URL url = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                finalJson = buffer.toString();
                return finalJson;


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Sorry we can't find the word!";

            } catch (IOException e) {
                e.printStackTrace();
                return "Sorry we can't find the word!";

            } finally {
                if(connection != null) connection.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            content.setText(finalData);

        }
    }

    public class JSONTask_post extends AsyncTask <String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            URL url = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject finalObject = new JSONObject(finalJson);
                explanation = finalObject.getString(params[1]);
                return explanation;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Sorry we can't find the word!";

            } catch (IOException e) {
                e.printStackTrace();
                return "Sorry we can't find the word!";

            } catch (JSONException e) {
                e.printStackTrace();
                return "Sorry we can't find the word!";

            } finally {
                if(connection != null) connection.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            finalData = new StringBuffer();
            String[] tokens = s.split(";|\\.");
            for(int i=0; i<tokens.length; i++){
                finalData.append(tokens[i]);
                finalData.append("\n\n");
            }
            content.setText(finalData);
            if(!explanation.equals("ERROR!!")) {
                // put the searched vocabulary to database
                int id_DB = -1;
                Cursor cursor = dbHelper.getAllRecords("default_user");
                if (cursor.getCount() != 0) {
                    cursor.moveToLast();
                    id_DB = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));// if input the same word, put into database 2 times in different id
                }
                long resultInsert = dbHelper.insert(++id_DB, user, "NO DATA", "NO DATA", inputString, 3, explanation);// id should to be increment auto
                if (resultInsert == -1) {
                    Toast.makeText(getApplicationContext(), "Some error occurred while inserting", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data inserted successfully" + resultInsert, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_login){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_documents){
            Intent intent = new Intent(MainActivity.this, DocumentsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_user){
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {

        super.onStop();
        dbHelper.closeDB();
    }
}

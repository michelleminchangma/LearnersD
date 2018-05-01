package com.example.michellema.learnersd;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michellema.learnersd.models.VocabularyModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DocumentsActivity extends AppCompatActivity {

    private String file_name;
    private EditText location;
    private Global get_user;
    private String user;
    private DBHelper dbHelper;
    private Cursor cursor;
    private Button btn_chercher;
    private Button btn_apprentissage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        location = findViewById(R.id.et_location);
        file_name = location.getText().toString();

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDB();
        get_user = Global.getInstance();
        user = get_user.getUser();

        btn_chercher = (Button) findViewById((R.id.btn_chercher));
        btn_apprentissage = (Button) findViewById((R.id.btn_apprentissage));

        btn_apprentissage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LearnActivity.class);
                startActivity(intent);
            }
        });

        btn_chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Toast.makeText(this, "Tips:\nCliquer sur les icons pour importer ou exporter le document...", Toast.LENGTH_SHORT).show();

    }

    public void save(View v) {

        cursor = dbHelper.getAllRecords(user);
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(file_name, MODE_PRIVATE);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String voc = cursor.getString(cursor.getColumnIndex(DBHelper.VOCABULARY));
                if(!voc.equals("NO DATA")) {
                    Float difficulty = cursor.getFloat(cursor.getColumnIndex(DBHelper.DIFFICULTY));
                    String explanation = cursor.getString(cursor.getColumnIndex(DBHelper.EXPLANATION));
                    fos.write((voc+"_@_").getBytes());
                    fos.write((""+difficulty).getBytes());
                    fos.write(("_@_"+explanation).getBytes());
                    fos.write("\n".getBytes());
                }
            }

            //location.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + file_name, Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // the file format is like:
    // vocabulary1_@_difficultyLevel_@_explanation
    // vocabulary2_@_difficultyLevel_@_explanation
    // ...
    // otherwise, it doesn't work
    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(file_name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            int id_DB = -1;
            cursor = dbHelper.getAllRecords("default_user");
            cursor.moveToLast();
            id_DB = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
            boolean error = false;

            while ((text = br.readLine()) != null) {
                //sb.append(text).append("\n");
                String[] line = text.split("_@_");
                try {
                    dbHelper.insert(++id_DB, user, "NO DATA", "NO DATA", line[0], Float.parseFloat(line[1]), line[2]);
                }catch (Exception e){
                    Toast.makeText(this, "Wrong file format!", Toast.LENGTH_SHORT).show();
                    error = true;
                }
            }

            //location.setText(sb.toString());
            if(!error){
                Toast.makeText(this, "import data successfully!", Toast.LENGTH_SHORT).show();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

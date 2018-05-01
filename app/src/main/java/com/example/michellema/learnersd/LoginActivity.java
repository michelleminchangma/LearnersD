package com.example.michellema.learnersd;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Cursor cursor;
    private TextView pas_de_compte;
    private TextView nip_oublie;
    private TextView contacter_nous;
    private EditText et_nom;
    private EditText et_nip;
    private Button btn_se_connecter;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pas_de_compte = (TextView) findViewById(R.id.tv_pas_de_compte);
        nip_oublie = (TextView) findViewById(R.id.tv_nip_oublie);
        contacter_nous = (TextView) findViewById(R.id.tv_contactez_nous);



        contacter_nous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {"dictionarylearners@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Phrases from user");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, I am the user of Learner D.");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });

        pas_de_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);

            }
        });

        nip_oublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetNIPActivity.class);
                startActivity(intent);
            }
        });

        et_nom = (EditText) findViewById(R.id.et_nom);
        et_nip = (EditText) findViewById(R.id.et_nip);

        btn_se_connecter = findViewById(R.id.btn_se_connecter);
        btn_se_connecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = et_nom.getText().toString().trim();
                Global get_user = Global.getInstance();
                user = get_user.getUser();

                // form the user_list and user_password_list
                ArrayList<String> user_list = new ArrayList<String>();
                ArrayList<String> user_password_list = new ArrayList<String>();
                dbHelper = new DBHelper(getApplicationContext());
                dbHelper.openDB();
                cursor = dbHelper.getUsers();
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    user_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.USER)));
                    user_password_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.PASSWORD)));

                }
                boolean found = false;
                for(int i=0; i<user_list.size(); i++){
                    if(user_list.get(i).equals(et_nom.getText().toString().trim())) {
                        if(user_password_list.get(i).equals(et_nip.getText().toString().trim())) {
                            found = true;
                            get_user.setUser(et_nom.getText().toString().trim());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "mauvais mot de pass...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if(!found){
                    Toast.makeText(getApplicationContext(), "il n'y a pas ce compte...", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }
}

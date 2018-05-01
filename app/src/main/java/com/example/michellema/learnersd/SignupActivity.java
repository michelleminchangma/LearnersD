package com.example.michellema.learnersd;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText et_nom1;
    private EditText et_email;
    private EditText et_nip1;
    private EditText et_nip2;
    private Button btn_enregistrer;
    private Cursor cursor;
    private ArrayList<String> user_list;
    private ArrayList<String> user_email_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_nom1 = (EditText) findViewById(R.id.et_nom1);
        et_email = (EditText) findViewById(R.id.et_email);
        et_nip1 = (EditText) findViewById(R.id.et_nip1);
        et_nip2 = (EditText) findViewById(R.id.et_nip2);
        btn_enregistrer = (Button) findViewById(R.id.btn_enregistrer);

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDB();

        user_list = new ArrayList<String>();
        user_email_list = new ArrayList<String>();

        // form the user_list and user_email_list
        cursor = dbHelper.getUsers();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            user_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.USER)));
            user_email_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.EMAIL)));

        }

        btn_enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), userCnt+"", Toast.LENGTH_SHORT).show();

                if((!et_nip1.getText().toString().trim().equals("")) &&
                        (!et_nip2.getText().toString().trim().equals("")) &&
                        et_nip1.getText().toString().trim().equals(et_nip2.getText().toString().trim())){

                    Toast.makeText(getApplicationContext(), "ok nip", Toast.LENGTH_SHORT).show();
                    if((!et_nom1.getText().toString().trim().equals("")) && !exist(user_list, et_nom1.getText().toString().trim())){

                        Toast.makeText(getApplicationContext(), "ok nom", Toast.LENGTH_SHORT).show();
                        if((!et_email.getText().toString().trim().equals("")) && !exist(user_email_list, et_email.getText().toString().trim())){

                            Toast.makeText(getApplicationContext(), "ok email", Toast.LENGTH_SHORT).show();
                            int id_DB = -1;
                            cursor = dbHelper.getAllRecords("default_user");
                            if (cursor.getCount() != 0) {
                                cursor.moveToLast();
                                id_DB = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));// if input the same word, put into database 2 times in different id
                            }else{
                                Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_SHORT).show();

                            }
                            dbHelper.insert(++id_DB, et_nom1.getText().toString().trim(), et_email.getText().toString().trim(), et_nip1.getText().toString().trim(), "NO DATA", 3, "NO DATA");
                            Global set_user = Global.getInstance();
                            set_user.setUser(et_nom1.getText().toString().trim());

                            Toast.makeText(getApplicationContext(), "ok, user added", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "email est vide ou email est inscript déjà...", Toast.LENGTH_SHORT).show();

                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "nom est vide ou nom existe déjà...", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "mots de pass ne sont pas la même...", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    public boolean exist(ArrayList<String> list, String item){
        for(int i=0; i<list.size(); i++){
            if(list.get(i).equals(item)) {
                return true;
            }
        }
        return false;
    }
}

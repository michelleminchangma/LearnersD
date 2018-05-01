package com.example.michellema.learnersd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class GetNIPActivity extends AppCompatActivity {

    //private Global get_global;
    private DBHelper dbHelper;
    //private String user;
    private Button btn_saisir;
    private EditText et_nom0;
    private EditText et_email0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_nip);

        dbHelper = new DBHelper(this);
        et_nom0 = (EditText) findViewById(R.id.et_nom0);
        et_email0 = (EditText) findViewById(R.id.et_email0);
        btn_saisir = (Button) findViewById(R.id.btn_saisir);
        btn_saisir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.openDB();
                //get_global = Global.getInstance();
                //user = get_global.getUser();
                Cursor cursor = dbHelper.getUsers();
                ArrayList<String> user_list = new ArrayList<String>();
                ArrayList<String> user_password_list = new ArrayList<String>();
                ArrayList<String> user_email_list = new ArrayList<String>();
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    user_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.USER)));
                    user_password_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.PASSWORD)));
                    user_email_list.add(cursor.getString(cursor.getColumnIndex(DBHelper.EMAIL)));

                }
                for(int i=0; i<user_list.size(); i++){
                    if(user_list.get(i).equals(et_nom0.getText().toString().trim())) {
                        if(user_email_list.get(i).equals(et_email0.getText().toString().trim())) {
                            //found = true;
                            //get_global.setUser(et_nom0.getText().toString().trim());
                            sendMessage(user_email_list.get(i), user_password_list.get(i));
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "mauvais email...", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "mauvais nom...", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

    }

    private void sendMessage(final String user_email, final String password) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GmailSender sender = new GmailSender("dictionarylearners@gmail.com", "Dl514573");
                    sender.sendMail("Retrieve your Learner D account password",
                            "Dear user, \n This is your password: " + password,
                            "dictionarylearners@gmail.com",
                            user_email);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

}

package com.example.michellema.learnersd;

import com.example.michellema.learnersd.models.VocabularyModel;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private List<VocabularyModel> voc_model_list = new ArrayList<>();
    private DBHelper dbHelper;
    private Cursor cursor;
    private Cursor c_difficulty;
    private Spinner sp_difficulty;
    private ListView lvMotAAprendre;
    private LearnActivity.MotAApprendreAdapter adapter;
    private int id;
    private Button btn_chercher;
    private Button btn_apprentissage;
    private Global get_user;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.openDB();
        sp_difficulty = (Spinner) findViewById(R.id.sp_difficulty);
        lvMotAAprendre = (ListView) findViewById(R.id.lv_mot_a_apprendre);
        btn_chercher = (Button) findViewById(R.id.btn_chercher);
        btn_apprentissage = (Button) findViewById(R.id.btn_apprentissage);

        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.difficulty_leveals));
        sp_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_difficulty.setAdapter(sp_adapter);
        sp_difficulty.setOnItemSelectedListener(this);

        get_user = Global.getInstance();
        user = get_user.getUser();

        btn_chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }

    // spinner item selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(getApplicationContext(), user, Toast.LENGTH_SHORT).show();

        String selected = parent.getItemAtPosition(position).toString();
        try{if(selected.equals("tout")){
            cursor = dbHelper.getAllRecords(user);
        }else {
            cursor = dbHelper.getDataBasedOnDifficulty(user, Float.parseFloat(selected));
        }
        voc_model_list.clear();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String voc = cursor.getString(cursor.getColumnIndex(DBHelper.VOCABULARY));
            if(!voc.equals("NO DATA")) {
                VocabularyModel motModel = new VocabularyModel();
                motModel.setMot(voc);
                motModel.setNiveauDifficulte(cursor.getFloat(cursor.getColumnIndex(DBHelper.DIFFICULTY)));
                voc_model_list.add(motModel);
            }
        }}catch (Exception e){
            Toast.makeText(getApplicationContext(), "il n'y a pas de data", Toast.LENGTH_SHORT).show();

        }
        adapter = new LearnActivity.MotAApprendreAdapter(getApplicationContext(), R.layout.layout_row, voc_model_list);
        lvMotAAprendre.setAdapter(adapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MotAApprendreAdapter extends ArrayAdapter {

        private List<VocabularyModel> motModelList;
        private int resource;
        private LayoutInflater inflater;

        public MotAApprendreAdapter(Context context, int resource, List<VocabularyModel> objects) {
            super(context, resource, objects);
            motModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            final TextView tvMot;
            RatingBar rbMot;

            tvMot = (TextView) convertView.findViewById(R.id.tv_mot_a_apprend);
            rbMot = (RatingBar) convertView.findViewById(R.id.rb_mot_a_apprend);

            tvMot.setText(motModelList.get(position).getMot());
            rbMot.setRating(motModelList.get(position).getNiveauDifficulte());

            rbMot.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    c_difficulty = dbHelper.getAllRecords(user);
                    for(c_difficulty.moveToFirst(); !c_difficulty.isAfterLast(); c_difficulty.moveToNext()) {
                        if(c_difficulty.getString(c_difficulty.getColumnIndex(DBHelper.VOCABULARY)).equals(motModelList.get(position).getMot())) {
                            id = c_difficulty.getInt(c_difficulty.getColumnIndex(DBHelper.ID));

                            // if difficulty is 1 stars, then delete this vocabulary from database
                            if(rating == 1){
                                dbHelper.delete(id);
                            }else{
                                dbHelper.update(id, rating);
                            }
                            Toast.makeText(getApplicationContext(), "difficulty leveal is changed", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            tvMot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c_difficulty = dbHelper.getAllRecords(user);
                    for(c_difficulty.moveToFirst(); !c_difficulty.isAfterLast(); c_difficulty.moveToNext()) {
                        if(c_difficulty.getString(c_difficulty.getColumnIndex(DBHelper.VOCABULARY)).equals(motModelList.get(position).getMot())) {
                            id = c_difficulty.getInt(c_difficulty.getColumnIndex(DBHelper.ID));
                            Toast.makeText(getApplicationContext(), c_difficulty.getString(c_difficulty.getColumnIndex(DBHelper.EXPLANATION)), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            return convertView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_login){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_documents){
            Intent intent = new Intent(getApplicationContext(), DocumentsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_user){
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

}

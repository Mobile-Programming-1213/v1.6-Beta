package ssjk.cafein;

/**
 * Created by wqe13 on 2016-11-23.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.Arrays;


public class BeginSearchActivity extends AppCompatActivity{

    //SQLiteDatabase cafedb;
    public String select;
    public Cursor cursor;
    //private ListView layout;
    //private CustomListAdapter m_Adapter;
    public SearchView searchView;
    private SuggestionsDatabase sdatabase;
    private DatabaseOpenHelper database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
        ListView layout;
        CustomListAdapter m_Adapter;
        layout = (ListView) findViewById(R.id.searchList);
        m_Adapter = new CustomListAdapter();
        layout.setAdapter(m_Adapter);
        sdatabase = new SuggestionsDatabase(this);
        database = new DatabaseOpenHelper(this);
        //cafedb = openOrCreateDatabase("cafesd1.db", Context.MODE_PRIVATE, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(position);
                int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);
                //searchView.setQuery(cursor.getString(indexColumnSuggestion), false);
                getsearch(cursor.getString(indexColumnSuggestion));

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String inquery) {
                getsearch(inquery);
                //customAdapter.swapCursor(cursor);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String innewText) {
                //when the user has entered text in the text field
                String newText = innewText.toUpperCase();
                Cursor cursor = sdatabase.getSuggestions(newText);
                if(cursor.getCount() != 0)
                {
                    String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION, SuggestionsDatabase.FIELD_TYPE };
                    int[] columnTextId = new int[] { android.R.id.text1, android.R.id.text2};

                    SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                            android.R.layout.simple_list_item_2, cursor,
                            columns , columnTextId
                            , 0);
                    //show suggestion
                    searchView.setSuggestionsAdapter(simple);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        return true;
    }

    public void getsearchEnd(String str1, String str2, String str3){
        Intent intent = new Intent(BeginSearchActivity.this, EndSearchActivity.class);
        //Name of the cafe/drink
        intent.putExtra("Name", str1);
        //Type of the drink
        intent.putExtra("Type" + 0, str2);
        //English name of the cafe with hyphens and lowercase
        intent.putExtra("eName", str3);
        //When more than one drink with a name exist
        if (j != 0){
            intent.putExtra("Number", j);
            for (int i = 0; i < j; i++){
                intent.putExtra("Type" + i+1, strarray[i]);
            }
        }
        startActivity(intent);
    }

    public void getsearch(String str){
        String query = str.toUpperCase();
        String type = null;
        select = String.format("SELECT * FROM Cafein WHERE Cafe_Name='%s' and Drink_Kind = 'Cafe_Info'", query);
        cursor = database.searchDatabase(select);
        cursor.moveToFirst();
        //when wanted information is not cafe
        if (cursor.getCount() == 0) {
            select = String.format("SELECT * FROM Cafein WHERE Drink_Name ='%s'", query);
            cursor = database.searchDatabase(select);
            cursor.moveToFirst();
            //check if there is another kind of drink with the same name
            if(cursor.getCount() != 0) {
                type = cursor.getString(9);
                String s = String.format("SELECT * FROM Cafein WHERE Drink_Name='%s' and Drink_Kind_app != '%s'", query, type);
                Cursor c = database.searchDatabase(s);
                int ccount = c.getCount();
                if (ccount != 0) {
                    c.moveToFirst();
                    strarray = new String[ccount];
                    for (int i = 0; i < ccount; i++, c.moveToNext()) {
                        //insert information into string array
                        if(!Arrays.asList(strarray).contains(c.getString(9))) {
                            strarray[j] = c.getString(9);
                            j++;
                        }
                    }
                }
                else{
                    strarray = null;
                    j = 0;
                }
                c.close();
            }
        }else {
            type = "CAFE";
        }
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(BeginSearchActivity.this, "No records found!", Toast.LENGTH_LONG).show();
        } else {
            if (type != null) {
                if (type.equals("CAFE")) {
                    getsearchEnd(cursor.getString(1), type, cursor.getString(10));
                } else {
                    Toast.makeText(BeginSearchActivity.this, j + 1 + "Search found!", Toast.LENGTH_LONG).show();
                    getsearchEnd(cursor.getString(3), type, null);
                }
            }
        }
    }

    public int j = 0;
    private String[] strarray;
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

package ssjk.cafein;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by wqe13 on 2016-11-27.
 */

public class EndSearchActivity extends AppCompatActivity{

    ListView layout;
    CustomListAdapter m_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        layout = (ListView) findViewById(R.id.endList);
        m_Adapter = new CustomListAdapter();
        layout.setAdapter(m_Adapter);
        String name = getIntent().getStringExtra("Name");
        String type = getIntent().getStringExtra("Type"+0);
        String Ename = getIntent().getStringExtra("eName");
        m_Adapter.add(name, type, Ename);
        int num = getIntent().getIntExtra("Number", 0);
        if (num != 0){
            for (int i = 0; i < num; i++)
                m_Adapter.add(name, getIntent().getStringExtra("Type" + i+1), null);
        }
    }
}

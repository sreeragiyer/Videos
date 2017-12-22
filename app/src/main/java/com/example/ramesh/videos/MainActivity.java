package com.example.ramesh.videos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<String>
 {

        Context ct;
        String cbnames[];
        public CustomAdapter(Context context, String[] n)
        {
            super(context,R.layout.checkbox_main,n);
            ct=context;
            cbnames=n;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = ((Activity)ct).getLayoutInflater();
            convertView = inflater.inflate(R.layout.checkbox_main, parent, false);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            cb.setText(cbnames[pos]);
            return convertView;
        }
        }

public class MainActivity extends AppCompatActivity {

    int checks;
    ListView lv;
    String cbnames[];
    ArrayList<String> query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView) findViewById(R.id.listView1);
        cbnames=new String[5];
        cbnames[0]="Sports";
        cbnames[1]="Music";
        cbnames[2]="Pranks";
        cbnames[3]="Science";
        cbnames[4]="Food";
        CustomAdapter cadapt=new CustomAdapter(this,cbnames);
        lv.setAdapter(cadapt);
        checks=0;
        query=new ArrayList<String>();
    }

    public void getData(View view)
    {
        CheckBox cb=(CheckBox) view.findViewById(R.id.checkBox1);
        boolean checked = cb.isChecked();
        if(checked)
        {
            checks++;
            query.add(cb.getText().toString());
        }
        else
        {
            checks--;
            query.remove(cb.getText().toString());
        }
        if(checks>3)
        {
            checks--;
            cb.setChecked(false);
            query.remove(cb.getText().toString());
            Toast.makeText(getApplicationContext(),"Max. 3 selections!",Toast.LENGTH_SHORT).show();
        }
    }

    public void getVids(View view)
    {
        String se="";
        int i;
        int len=query.size();
        for(i=0;i<len;i++)se+=query.get(i)+"+";
        se=se.substring(0,se.length()-1);
        Intent in=new Intent(this, VidShow.class);
        in.putExtra("query",se);
        startActivity(in);
    }
}

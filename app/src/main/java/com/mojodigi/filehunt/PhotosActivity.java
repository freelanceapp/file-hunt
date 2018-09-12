package com.mojodigi.filehunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mojodigi.filehunt.Adapter.GridViewAdapter;
//

import static com.mojodigi.filehunt.Class.Constants.PATH;


public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;
    ActionMode mActionMode;
    @Override


    //this file is jot in use as the new vesrion of this using Recycleview has benn made
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity);
        gridView = (GridView)findViewById(R.id.gv_folder);

        int_position = getIntent().getIntExtra("value", 0);

            //ArrayList<String> ImagesList = Category_Explore_Activity.al_images.get(int_position).getAl_imagepath();

        adapter = new GridViewAdapter(this,Category_Explore_Activity.al_images,int_position);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setAdapter(adapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), SingleViewMediaActivity.class);
                intent.putExtra(PATH,Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(i));
                startActivity(intent);
            }
        });


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;



            }
        });



    }


}

package com.captain.ak.faceattributes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.FileNotFoundException;



    public class ResultActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_result);

            //ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);
            Log.i("Result Activity " , "working fine");

            ///String encodedImage = getIntent().getStringExtra("image");

            // byte[] byteArray = Base64.decode(encodedImage,Base64.DEFAULT);

            Bitmap originalBitmap = null;
            try {
                originalBitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }

            String data = getIntent().getStringExtra("list_faces");
            Gson gson = new Gson();

            Face[] faces = gson.fromJson(data,Face[].class);
            ListView listView = (ListView)findViewById(R.id.listView);


            CustomAdapter customAdapter = new CustomAdapter(faces,this,originalBitmap);

            listView.setAdapter(customAdapter);

        }
    }

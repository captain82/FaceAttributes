package com.captain.ak.faceattributes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    // Replace `<API endpoint>` with the Azure region associated with
// your subscription key. For example,
// apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    private final String apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";

    // Replace `<Subscription Key>` with your subscription key.
// For example, subscriptionKey = "0123456789abcdef0123456789ABCDEF"
    private final String subscriptionKey = "ed19d459276a4a5e9cbce6f80d14f836";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    //Button button2 = (Button)findViewById(R.id.button2);
    ImageView imageView1;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, "Select Picture"), PICK_IMAGE);
            }
        });



        detectionProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("do in background" , "working fine");

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);
                imageView1 = (ImageView)findViewById(R.id.imageView1);
                imageView1.setImageBitmap(bitmap);

                // Comment out for tutorial
                detectAndFrame(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






    // Detect faces by uploading a face image.
// Frame faces after detection.
    private void detectAndFrame( Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            //for checking attribute of a pic
                            FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]
                                    {
                                            FaceServiceClient.FaceAttributeType.HeadPose,
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender,
                                            FaceServiceClient.FaceAttributeType.Smile,
                                            FaceServiceClient.FaceAttributeType.FacialHair,

                                    };



                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    faceAttr          // returnFaceAttributes:
                                /* new FaceServiceClient.FaceAttributeType[] {
                                    FaceServiceClient.FaceAttributeType.Age,
                                    FaceServiceClient.FaceAttributeType.Gender }
                                */
                            );
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                        Log.i("onProgress update" , "working fine");
                    }
                    @Override
                    protected void onPostExecute(Face[] faces) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();



                       /* ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                        byte[] byteArray = bStream.toByteArray();

                        String encodedImage = Base64.encodeToString(byteArray,Base64.DEFAULT);*/


                        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                        Gson gson = new Gson();
                        String data = gson.toJson(faces);
                        intent.putExtra("list_faces",data);

                        //ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                        //byte[] byteArray = bStream.toByteArray();

                        //intent.putExtra("image" , encodedImage);
                        //intent.putExtra("byteArray",bStream.toByteArray());

                        String fileName = "myImage";//no .png or .jpg needed
                        try {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                            fo.write(bytes.toByteArray());
                            // remember close file output
                            fo.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            fileName = null;
                        }

                        //intent.putExtra("BitmapImage",imageBitmap);
                        Log.i("onPostExecute " , "working fine");
                        startActivity(intent);

                       /* ImageView imageView = findViewById(R.id.imageView1);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();*/


                    }
                };
        detectTask.execute(inputStream);
    }

   /* private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }*/

    /*private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }*/
}

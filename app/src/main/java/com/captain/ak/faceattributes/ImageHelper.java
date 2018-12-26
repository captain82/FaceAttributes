package com.captain.ak.faceattributes;

import android.graphics.Bitmap;

import com.microsoft.projectoxford.face.contract.FaceRectangle;

public class ImageHelper {

    public static FaceRectangle calculateFaceRectangle(Bitmap bitmap, FaceRectangle faceRectangle, double faceRectEnlargeRation)
    {
        //Get the resized side length of the face rectangel
        double sideLength = faceRectangle.width*faceRectEnlargeRation;
        sideLength=Math.min(sideLength,bitmap.getWidth());
        sideLength = Math.min(sideLength,bitmap.getHeight());

        //Make the leftedge to left more
        double left = faceRectangle.left - faceRectangle.width*(faceRectEnlargeRation-1.0)*0.5;
        left = Math.max(left,0.0);
        left = Math.min(left,bitmap.getWidth() - sideLength);

        //Make the top edge to top more
        double top = faceRectangle.top - faceRectangle.height*(faceRectEnlargeRation-1.0)*0.5;
        top = Math.max(top,0.0);
        top = Math.min(top,bitmap.getWidth() - sideLength);

        //Shift the top edge to top more for better view for humans

        double shiftTop = faceRectEnlargeRation-1.0;
        shiftTop = Math.max(shiftTop,0.0);
        shiftTop = Math.min(shiftTop,1.0);
        top-=0.15*shiftTop*faceRectangle.height;
        top = Math.max(top,0.0);

        //Set the result

        FaceRectangle result = new FaceRectangle();
        result.left = (int)left;
        result.top = (int)top;
        result.width = (int)sideLength;
        result.height = (int)sideLength;
        return result;
    }

    public static Bitmap generateThumbnail(Bitmap originalBitmap,FaceRectangle faceRectangle)
    {
        FaceRectangle face = calculateFaceRectangle(originalBitmap , faceRectangle , 1.3);
        return Bitmap.createBitmap(originalBitmap,faceRectangle.left,faceRectangle.top,faceRectangle.width,faceRectangle.height);

    }
}

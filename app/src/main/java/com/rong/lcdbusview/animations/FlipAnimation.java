package com.rong.lcdbusview.animations;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimation extends Animation {
    private int centerX, centerY;
    private int width, height;
    private float startDegree, endDegree;
    {
        centerX = 0 ;
        centerY = 0 ;
    }

    public FlipAnimation(int w, int h, float startDegree, float endDegree) {
        this.width = w;
        this.height = h;
        this.centerX = w>>1;
        this.centerY = h >> 1;
        this.startDegree = startDegree;
        this.endDegree = endDegree;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int translateX = 0 ;
        int translateZ = 0 ;
        int translateY = 0;

        double a = (endDegree - startDegree) * interpolatedTime + startDegree + 90;
//        Log.d("3d", endDegree + "");
//        Log.d("3d", startDegree + "");
//        Log.d("3d", Math.cos(a) + "");
        //Math.cos是弧度制的
        translateX = (int) (- (width >> 1) * Math.cos(a * 3.1415926 / 180));
        translateZ = (int) (- (height >> 1) * Math.sin(a * 3.1415926 / 180 ) + (height >> 1 ));

        Matrix matrix = t.getMatrix();
        Camera camera = new Camera();
        camera.save();
        camera.translate(0,0,translateZ);
        camera.rotateX(startDegree + (endDegree - startDegree) * interpolatedTime);
        camera.getMatrix(matrix);
        camera.restore();

        //设置旋转的中心点
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
//        matrix.postTranslate(translateX,translateY);
    }
}


package hr.riteh.nksproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;


public class ArrowTimeView extends View {

    int availableWidth;
    int availableHeight;
    int currentTarget;
    float positionX;
    float positionY;
    float targX;
    float targY;
    float centerX,centerY;
    float speedX;
    float maxSpeed;
    float ballRadius;
    float speedStep;
    Bitmap ballBitmap;
    Bitmap squareBitmap;
    Paint myPaint;
    private ballInWallListener myBallInWallListener;
    ArrayList<PointF> myTargets;


    public ArrowTimeView(Context context) {
        super(context);

            targX=0;
            targY=0;
            centerX=0;
            centerY=0;
            positionX = 0;
            positionY = 0;
            currentTarget=0;
            speedX = 0;
            maxSpeed = 20;
            speedStep = maxSpeed / 30;
            myPaint = new Paint();
            ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ballv2);
            squareBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.black_square);
            myTargets = new ArrayList<>();
        }

    // Sucelje za listener kolizije
    public interface ballInWallListener {
        void onBallInWall(String input);
    }

    public void setBallInWallListener(ballInWallListener inputListener){
        this.myBallInWallListener = inputListener;
    }

    public void setTargets(int w, int h){
        float y = centerY-ballRadius;
        myTargets.add(new PointF(w/18 * 7, y));
        myTargets.add(new PointF(w/18 * 1, y));
        myTargets.add(new PointF(w/18 * 13, y));
        myTargets.add(new PointF(w/18 * 4, y));
        myTargets.add(new PointF(w/18 * 15, y));
        myTargets.add(new PointF(w/18 * 8, y));
        myTargets.add(new PointF(w/18 * 6, y));
        myTargets.add(new PointF(w/18 * 14, y));
        myTargets.add(new PointF(w/18 * 17, y));
        myTargets.add(new PointF(w/18 * 12, y));
        myTargets.add(new PointF(w/18 * 10, y));
        myTargets.add(new PointF(w/18 * 3, y));
        myTargets.add(new PointF(w/18 * 11, y));
        myTargets.add(new PointF(w/18 * 2, y));
        myTargets.add(new PointF(w/18 * 5, y));
        myTargets.add(new PointF(w/18 * 7, y));
        myTargets.add(new PointF(w/18 * 15, y));
        myTargets.add(new PointF(w/18 * 12, y));
        myTargets.add(new PointF(w/18 * 10, y));
        myTargets.add(new PointF(w/18 * 5, y));
    }

    public void setBallPosition(int x, int y){
        this.positionX = x;
        this.positionY = y;
    }

    public void setTargetPosition(){
        this.targX = myTargets.get(currentTarget).x;
        this.targY = myTargets.get(currentTarget).y;
    }
    public void nextTarget(){
        if(myTargets.size()-1>currentTarget){
            currentTarget++;
        }
    }
    public  void setCenterPosition(int x,int y){
        this.centerX = x;
        this.centerY = y;
    }

    public void setBallRadius(float radius){
        this.ballRadius = radius;
        int d = Math.round(radius * 2);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, d, d, false);
        squareBitmap = Bitmap.createScaledBitmap(squareBitmap,2*d,2*d,false);
    }

    public void moveBall(int flag){

        if ((availableWidth == 0) || (availableHeight == 0)) return;

        if(flag == 1) {
            speedX = speedX + speedStep;
            if (speedX > maxSpeed) speedX = maxSpeed;
        }else if(flag == 2){
            speedX = speedX - speedStep;
            if (speedX < -maxSpeed) speedX = -maxSpeed;
        }else if(flag == 3){
            if( speedX > 0){
                speedX = speedX - speedStep;
                if (speedX < 0) speedX = 0;
            }else if(speedX < 0) {
                speedX = speedX + speedStep;
                if (speedX > 0) speedX = 0;
            }
        }
        positionX = positionX + speedX;

        //Provjera kolizije sa zidom,zaljepi lopticu za zid
        //Desni zid
        if (positionX + ballRadius > availableWidth) {
            positionX = availableWidth - ballRadius;
        }
        //Lijevi zid
        if (positionX - ballRadius < 0) {
            positionX = 0 + ballRadius;
        }

        invalidate();
       //Detekcija je li lopta unutar okvira
        int obstacleIndex = isBallInTarget();
        if (obstacleIndex != -1){
            if(obstacleIndex == 1) {
                if (myBallInWallListener != null)
                    myBallInWallListener.onBallInWall("inside");
            }else  if(obstacleIndex == 2){
                if (myBallInWallListener != null)
                    myBallInWallListener.onBallInWall("outside");
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.availableWidth = w;
        this.availableHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myPaint.setColor(Color.WHITE);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, availableWidth, availableHeight, myPaint);
        canvas.drawBitmap(ballBitmap, positionX - ballRadius, positionY - ballRadius, myPaint);
        canvas.drawBitmap(squareBitmap,targX-2*ballRadius,targY-2*ballRadius,myPaint);
    }

    public int isBallInTarget(){
        if (myTargets.size() <=0) return -1;

            PointF targetLocation = myTargets.get(currentTarget);
            float leftBorder = targetLocation.x - ballRadius/2;
            float rightBorder = targetLocation.x + ballRadius/2;

            if ((positionX  >= leftBorder) &&
                    (positionX  <= rightBorder))
            {
                return 1; //unutar okvira
            }
        return 2; // izvan okvira
    }
}

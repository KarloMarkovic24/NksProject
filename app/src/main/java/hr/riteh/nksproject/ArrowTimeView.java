package hr.riteh.nksproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class ArrowTimeView extends View {

    int availableWidth, availableHeight;
    float positionX, positionY,targX,targY;
    float centerX,centerY;
    float speedX;
    float maxSpeed;
    float ballRadius;
    float speedStep;
    Bitmap ballBitmap;
    Bitmap squareBitmap;
    Paint myPaint;
    private ballInWallListener myBallInWallListener;

    
    Random r;
    ArrayList<PointF> myObstacles;          // kolekcija s informacijama o life-up "objektu" (POZICIJA)


    public ArrowTimeView(Context context) {
        super(context);
            targX=0;
            targY=0;
            centerX=0;
            centerY=0;
            positionX = 0;
            positionY = 0;
            speedX = 0;
            maxSpeed = 20;
            speedStep = maxSpeed / 30;
            myPaint = new Paint();
            ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ballv2);
            squareBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.black_square);
            myObstacles = new ArrayList<>();            // Gdje ce se prilike pojavljivati (pozicije)
        }



    // Sucelje za listener kolizije
    public interface ballInWallListener {
        void onBallInWall(String input);
    }

    public void setBallInWallListener(ballInWallListener inputListener){
        this.myBallInWallListener = inputListener;
    }


    // Postavljanje pozicije loptice:
    public void setBallPosition(int x, int y){
        this.positionX = x;
        this.positionY = y;
    }

    public void setTargetPosition(int x, int y){
        this.targX = x;
        this.targY = y;
    }
    public  void setCenterPosition(int x,int y){
        this.centerX = x;
        this.centerY = y;
    }

    public void setBallRadius(float radius){
        this.ballRadius = radius;
        int d = Math.round(radius * 2);
        // skaliranje slike; inace moze biti zahtjevno za memorijske resurse:
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, d, d, false);
        squareBitmap = Bitmap.createScaledBitmap(squareBitmap,2*d,2*d,false);
    }


    public void MoveBall(int flag){

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


        // Provjera kolizije:
        boolean uZiduSam = false;



        // Po detekciji kolizije, generira se odgovarajuci event:
        if (uZiduSam) {
            if (myBallInWallListener != null)
                myBallInWallListener.onBallInWall("wall");
        }
        invalidate();

        // Detekcija kolizije s "novim zivotom": DETEKCIJA S OBJEKTOM KOJEG STVARAMO TJ ZADATKOM
        int obstacleIndex = isBallInObstacle();
        if (obstacleIndex != -1){
            // Pronadjena je kolizija unutar regularnog vremena,
            // moguce je odgovarajucu ikonu maknuti s ekrana (ta life-up mogucnost se
            // brise iz kolekcije mogucnosti), a zaradjen je novi zivot (to treba dojaviti MainActivity-ju)


            // podizanje eventa, sada s drugim parametrom (obstacle):
            if (myBallInWallListener !=null)
                myBallInWallListener.onBallInWall("obstacle");
        }
    }


    // Promjena smjera loptice zasnovana na "vanjskoj" komandi (upravaljanje):  DRUKCIJE NAPISAT
    public void invokeCommand(int command){
        // Lijevo:
        if (command==0) {
            positionX=positionX-2;
        }

        // Desno:
        if (command==1) {
            positionX=positionX+2;
        }

        invalidate();
    }


    // Event nakon kojeg aplikacija "zna" raspolozive dimenzije za doticni view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.availableWidth = w;
        this.availableHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }


    // Metoda koja "crta" view
    // (svaki custom view s "nestandardnim opisom sucelja" trebao bi nadjacati ovu metodu):
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Racunamo vremena i pozicije za life-up "objekte";
        // to se treba uciniti samo na pocetku igre:


        // Bijela pozadina:
        myPaint.setColor(Color.WHITE);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, availableWidth, availableHeight, myPaint);

        // Bouncing box:
        myPaint.setStrokeWidth(5f);
        myPaint.setColor(Color.BLUE);
        myPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, availableWidth, availableHeight, myPaint);

        // Loptica (ako se koristi neki postojeci drawable):
        canvas.drawBitmap(ballBitmap, positionX - ballRadius, positionY - ballRadius, myPaint);
        canvas.drawBitmap(squareBitmap,targX-2*ballRadius,targY-2*ballRadius,myPaint);


    }





    // Generiranje i spremanje informacija o life-up mogucnostima;
    // Informacije se cuvaju u dvije kolekcije (jedna cuva pozicije, a druga vremena pojavljivanja):
    public void add_obstacle(long momentToShow){
        float minX = ballRadius * 2;
        float maxX = (float)availableWidth - ballRadius * 2;
        float minY = ballRadius * 2;
        float maxY = (float)availableHeight - ballRadius * 2;

        // Random pozicija za pojavljivanje:
        float random_x = r.nextFloat() * (maxX - minX) + minX;
        float random_y = r.nextFloat() * (maxY - minY) + minY;
        PointF newPoint = new PointF(random_x, random_y);

        myObstacles.add(newPoint);

    }


    // Provjera: je li se loptica sudarila s life-up slikom?
    // Vraca -1 ako nema kolizije, u protivnom vraca index iz kolekcije 'myObstacles'
    // koji oznacava trenutak kada se odgovarajuca life-up mogucnost prikazala
    public int isBallInObstacle(){
        // Ako nema relevenatnih life-up informacija, kolizija se ne moze dogoditi:
        if (myObstacles.size() <=0)
            return -1;

        // Na ovom mjestu uvijek imamo azuriranu kolekciju koja sadrzi samo validne life-up informacije
        // (ostali se s vremenom brisu, ili zbog time-expire, ili jer su uspjesno "pokupljeni"):
        for (int i = 0; i < myObstacles.size(); i++) {

            // Pronadjeno je "regularno" vrijeme u kojem je na zaslonu sigurno prikazana
            // life-up mogucnost (u protivnom je ne bi bilo u kolekciji).
            // Sada mozemo provjeriti uvjete sudara (pozicija loptice X life-up pozicija):
            PointF iObstacle = myObstacles.get(i);
            float leftBorder = iObstacle.x - ballRadius * 2;
            float rightBorder = iObstacle.x + ballRadius * 2;


            if ((positionX + ballRadius >= leftBorder) &&
                    (positionX + ballRadius <= rightBorder))
            {
                return i;
            }

            if ((positionX - ballRadius >= leftBorder) &&
                    (positionX - ballRadius <= rightBorder))
            {
                return i;
            }

        }

        return -1;
    }

}

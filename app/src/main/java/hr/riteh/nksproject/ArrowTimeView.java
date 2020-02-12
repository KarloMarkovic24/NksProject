package hr.riteh.nksproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class ArrowTimeView extends View {

    int availableWidth, availableHeight;    // raspolozive dimenzije ovog view-a
    float positionX, positionY;             // pozicija loptice;
     float    speedX;       // brzina loptice
    float ballRadius;                       // dimenzija loptice
    Bitmap ballBitmap;                      // loptica (slika)
                // trenutna boja loptice (ako se crta "na ruke")
    Paint myPaint;                          // podrska za crtanje
    private ballInWallListener myBallInWallListener;    // listener za detekciju kolizije


    // Podrska za upravljanje mogucnoscu dobivanja novog zivota:
    Random r;
    ArrayList<PointF> myObstacles;          // kolekcija s informacijama o life-up "objektu" (POZICIJA)
    ArrayList<Long> myObstacleShowTimes;    // kolekcija s informacijama o life-up "objektu" (VRIJEME POJAVLJIVANJA)
    long currentTime = 0;
    long LIFE_UP_EXPIRATION_TIME = 6000;    // mogucnost za pokupiti novi zivot "traje" 6s

    public ArrowTimeView(Context context) {
        super(context);

            positionX = 0;
            positionY = 0;
            myPaint = new Paint();
            ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ballv2);

            myObstacles = new ArrayList<>();            // Gdje ce se prilike pojavljivati (pozicije)
            myObstacleShowTimes = new ArrayList<>();    // Kada ce se prilike pojavljivati (vremena)
        }



    // Sucelje za listener kolizije
    public interface ballInWallListener {
        void onBallInWall(String input);
    }

    public void setBallInWallListener(ballInWallListener inputListener){
        this.myBallInWallListener = inputListener;
    }






    /*
    // Kada bi ovaj view element ukljucili u xml definiciju sucelja, primjerice kao:
    // <hr.rma.sl.bouncingball.BounceView>...
    // ...onda bi nam trebala definicija i ovakvog konstruktora (koji bi mogao primiti
    // atribute view-a opisanih u xml-u):
    public BounceView(Context context, AttributeSet attrs){
        super(context, attrs);
        // ...
    }
    */






    // Postavljanje pozicije loptice:
    public void setBallPosition(int x, int y){
        this.positionX = x;
        this.positionY = y;
    }


    // Postavljanje dimenzije loptice:
    public void setBallRadius(float radius){
        this.ballRadius = radius;
        int d = Math.round(radius * 2);
        // skaliranje slike; inace moze biti zahtjevno za memorijske resurse:
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, d, d, false);

    }




    // Izracun nove pozicije loptice zasnovan na inkrementalnom pomaku:
    public void MoveBall(int flag){
        // Ako zaslon jos nije "poznat" aplikaciji:
        if ((availableWidth == 0) || (availableHeight == 0)) return;
            positionX=positionX+speedX;
        if(flag == 1) {

            // Promjena pozicije (inkrementalno) u odnosnu na vektor brzine:
            speedX = speedX+2;
        }else if(flag == 2){
            speedX=speedX-2;
        }else if(flag == 3){
            if( speedX >0){
                speedX=speedX-2;
                }
            }else if(speedX <0){
            speedX=speedX-2;
        }

        // Provjera kolizije:
        boolean uZiduSam = false;

        // Kolizija; desni zid:
        if (positionX + ballRadius > availableWidth) {
            positionX = availableWidth - ballRadius;
            //speedX = -speedX;
            uZiduSam = true;
        }

        // Kolizija; lijevi zid:
        if (positionX - ballRadius < 0) {
            positionX = 0 + ballRadius;
            //speedX = -speedX;
            uZiduSam = true;
        }


        // Po detekciji kolizije, generira se odgovarajuci event:
        if (uZiduSam) {
            if (myBallInWallListener != null)
                myBallInWallListener.onBallInWall("wall");
        }
/**
        // Detekcija kolizije s "novim zivotom": DETEKCIJA S OBJEKTOM KOJEG STVARAMO TJ ZADATKOM
        int obstacleIndex = isBallInObstacle();
        if (obstacleIndex != -1){
            // Pronadjena je kolizija unutar regularnog vremena,
            // moguce je odgovarajucu ikonu maknuti s ekrana (ta life-up mogucnost se
            // brise iz kolekcije mogucnosti), a zaradjen je novi zivot (to treba dojaviti MainActivity-ju)
            remove_obstacle(obstacleIndex);

            // podizanje eventa, sada s drugim parametrom (obstacle):
            if (myBallInWallListener !=null)
                myBallInWallListener.onBallInWall("obstacle");
        }***/
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

        // Loptica (ako se crta "na ruke"):
        //myPaint.setColor(this.currentBallColor);
        //myPaint.setStyle(Paint.Style.FILL);
        //myPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //canvas.drawCircle(positionX, positionY, ballRadius, myPaint);

        // Loptica (ako se koristi neki postojeci drawable):
        canvas.drawBitmap(ballBitmap, positionX - ballRadius, positionY - ballRadius, myPaint);



    }



    //*** Podrska za nove zivote ***//

    // Brisanje i ponovno generiranje svih informacija vezanih za nove zivote



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
        myObstacleShowTimes.add(momentToShow);
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
            long beginTime = myObstacleShowTimes.get(i);
            long endTime = beginTime + LIFE_UP_EXPIRATION_TIME;
            if (!((this.currentTime >= beginTime) &&
                    (this.currentTime <= endTime))) {
                continue;
            }

            // Pronadjeno je "regularno" vrijeme u kojem je na zaslonu sigurno prikazana
            // life-up mogucnost (u protivnom je ne bi bilo u kolekciji).
            // Sada mozemo provjeriti uvjete sudara (pozicija loptice X life-up pozicija):
            PointF iObstacle = myObstacles.get(i);
            float leftBorder = iObstacle.x - ballRadius * 2;
            float rightBorder = iObstacle.x + ballRadius * 2;
            float upperBorder = iObstacle.y - ballRadius * 2;
            float bottomBorder = iObstacle.y + ballRadius * 2;

            if ((positionX + ballRadius >= leftBorder) &&
                    (positionX + ballRadius <= rightBorder) &&
                    (positionY + ballRadius >= upperBorder) &&
                    (positionY + ballRadius <= bottomBorder))
            {
                return i;
            }

            if ((positionX - ballRadius >= leftBorder) &&
                    (positionX - ballRadius <= rightBorder) &&
                    (positionY - ballRadius >= upperBorder) &&
                    (positionY - ballRadius <= bottomBorder))
            {
                return i;
            }

            if ((positionX - ballRadius >= leftBorder) &&
                    (positionX - ballRadius <= rightBorder) &&
                    (positionY + ballRadius >= upperBorder) &&
                    (positionY + ballRadius <= bottomBorder))
            {
                return i;
            }

            if ((positionX + ballRadius >= leftBorder) &&
                    (positionX + ballRadius <= rightBorder) &&
                    (positionY - ballRadius >= upperBorder) &&
                    (positionY - ballRadius <= bottomBorder))
            {
                return i;
            }
        }

        return -1;
    }


    // Brisanje life-up mogucnosti iz odnosnih kolekcija:
    public void remove_obstacle(int obstacleToRemove){
        // Uvijek moramo paziti da azuriramo obje kolekcije
        // (ovo se moze elegantnije rijesiti, na nacin da stvorimo poseban razred koji bi sadrzavao
        // i vrijeme i poziciju za life-up mogucnost):
        if (myObstacles.size() > 0)
        {
            myObstacles.remove(obstacleToRemove);
            myObstacleShowTimes.remove(obstacleToRemove);
        }
    }


}

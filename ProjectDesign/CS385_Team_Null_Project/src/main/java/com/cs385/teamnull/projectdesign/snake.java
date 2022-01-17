package com.cs385.teamnull.projectdesign;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.cs385.teamnull.projectdesign.Labyrinth.LabLevelManager;

import static com.cs385.teamnull.projectdesign.Constants.musicSetting;
/**
 * Created by Ruiqi Li on 2017/12/10.
 *
 *This is a upper java file to control the running of SnakeView.java.
 *The handler will send a message to the UI thread under a fixed frequency to refresh the screen. There is a infinite while loop in the 
 *main thread to repeat "receive message - sleep" loop over and over again. In "receive message"mode, the thread will run the message 
 *obtained and in "sleep"mode, the Thread.sleep() will block the thread for a while of time. 
 *The effect is that the game view will be refreshed in predefined frequency, which will make the elements look like "moving" on the 
 *screen.
 *
 *This control file will also get the information of width, height and density of the screen and set the music playing while the game 
 *running.
 *
 *Reference:
 *http://blog.csdn.net/poorkick/article/details/51203618
 * @author Ruiqi Li
 * @author student ID : 17251911
 * @author Katie Brugha
 * @author student ID : 17186293
 * @version 20-1-2018
 */
public class snake extends AppCompatActivity {

    private SnakeView mSnakeView;
    
    //set the refresh interval time
    //the effect is how fast the elements "move" on the screen
    private static final int REFRESH = 1;
    private static final int REFRESHINTERVAL = 200;
    private boolean isWin = false;

    //create mediaplayer for later use
    private MediaPlayer mediaPlayer;
    private boolean playing;

  /*
   *Handler will send a message to UI thread while game running. 
   *If the message contains the integer variable REFRESH and the SnakeView has already created, the invalidate() method will pop the old 
   *view out and re-run the onDraw() method in SnakeView to draw all elements on the screen again.
   */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == REFRESH) {
                if (mSnakeView != null)
                    mSnakeView.invalidate();//if SnakeView is not NULL, then throw it and rewrite the onDraw() method in it.
            }
        }
    };

    //width, height and density information of the screen
    public static int snakewidth ;
    public static int snakeheight ;
    public static float snakedensity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the game window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //if the music player has created, play the background music
        mediaPlayer = MediaPlayer.create(this, R.raw.snake);
        if(musicSetting){
            playing = true;
            mediaPlayer.start();
        }

        mSnakeView = new SnakeView(this);
        setContentView(mSnakeView);
        
        //create a new thread called GameThread and start() method will run the thread
        new Thread(new GameThread()).start();

    }

  /*
   *Inner class - GameThread
   *the GameThread implements the interface - Runnable. The most important overrided method is run() which will control the game running.
   *When isWin condition has not met, the Message msg will receive the message from handler and send it to UI thread to rewrite the 
   *onDraw() method, and then the thread will "sleep" (be blocked) for the predefined peroid.
   *If the player has already win the game, 3 successful consecutive "eaten" in the game, the game window will jump to the CatchGame and
   *the snake game will be finished immediately.
   */
    class GameThread implements Runnable {

        @Override
        public void run() {
        while(!isWin )
        {
                if(mSnakeView.win){
                    isWin = true;
                    Intent intent = new Intent(snake.this,CatchGame.class);
                    startActivity(intent);
                    finish();
                }
                Message msg = mHandler.obtainMessage();
                msg.arg1 = REFRESH;
                mHandler.sendMessage(msg);
                try{
                    Thread.sleep(REFRESHINTERVAL);//
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
 /**
   *override public method
   *This method will work when back button is pressed 
   *When the back button is pressed, the game activity is finished and invisible. The upper game level will run the terminate() method 
   *and mediaplayer object is released, so the other game can use mediaplayer
   */
    @Override
    public void onBackPressed(){
        LabLevelManager.terminate();
        playing = false;
        mediaPlayer.release();
        finish();
    }
    
 /**
   *override protected method
   *This method will work when the game is paused by the other app for instance. 
   *It will check if the music is playing, if true, then pauses the music
   */
    @Override
    protected void onPause(){
        super.onPause();
        if(musicSetting&&playing){ //checks if the music is playing, if true, it pauses it
            mediaPlayer.pause();
        }
    }

 /**
   *override public method
   *This method will work when the game is resumed, like if you back into the game from the other app.
   *If you resume the game, the music will start again from where you pause it.
   */
    @Override
    public void onResume(){
        super.onResume();
        if(musicSetting&&playing){ //if youve muted the game or playing is true (if you exit game and get back into it)
            mediaPlayer.start(); //start music
        }
    }
}

package fr.flafla.android.urbi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import fr.flafla.android.urbi.control.Joystick;
import fr.flafla.android.urbi.robot.FakeRobot;
import fr.flafla.android.urbi.robot.Robot;
import fr.flafla.android.urbi.robot.Robot.ImageHandler;

public class MainActivity extends Activity {
//	private class RobotEvent implements OnClickListener {
//		public final int trackL;
//		public final int trackR;
//		public RobotEvent(int trackL, int trackR) {
//			super();
//			this.trackL = trackL;
//			this.trackR = trackR;
//		}
//		public void onClick(View v) {
//			robot.go(MainActivity.this, trackL, trackR);
//		}
//	}
	

	// private Robot robot;
	//	
	// private Bitmap bitmap;
	

	// private CommandSurface surface;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Robot.actuel = new FakeRobot();

		// surface = new CommandSurface(this);
		// setContentView(surface);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		LinearLayout layout = (LinearLayout) findViewById(R.id.panel);
		int size = 220;
		layout.addView(new Joystick(this), size, size);
		layout.addView(new Joystick(this), size, size);
        
        Robot.actuel.acquire(this, new ImageHandler() {
			public void handle(Bitmap bitmap) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.panel);
				BitmapDrawable drawable = new BitmapDrawable(bitmap);
				layout.setBackgroundDrawable(drawable);
			}
		});
        
     
        
        
//        Button left = (Button) findViewById(R.id.left);
//        Button right = (Button) findViewById(R.id.right);
//        Button walk = (Button) findViewById(R.id.walk);
//        Button stop = (Button) findViewById(R.id.stop);
//        walk.setOnClickListener(new RobotEvent(50, 50));
//        stop.setOnClickListener(new RobotEvent(0, 0));
//        left.setOnClickListener(new RobotEvent(50, 0));
//        right.setOnClickListener(new RobotEvent(0, 50));
        
        
    }
    
    
    
}
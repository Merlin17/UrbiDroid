package fr.flafla.android.urbi;

import static fr.flafla.android.urbi.log.LoggerFactory.logger;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import fr.flafla.android.urbi.control.Camera;
import fr.flafla.android.urbi.control.Camera.ImageHandler;
import fr.flafla.android.urbi.control.Joystick;
import fr.flafla.android.urbi.robot.Robot;

public class MainActivity extends Activity {
	
	Handler imageHandler = new Handler();

	Runnable imageDisplayer = new Runnable() {
		@Override
		public void run() {
			// display the image on background
			logger().i("Main", "image");
			VideoSurfaceView videoView = (VideoSurfaceView) findViewById(R.id.video);
			videoView.setBitmap(new BitmapDrawable(bitmap));
			bitmap = null;
		}
	};

	protected InputStream bitmap;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Robot.actuel = new FakeRobot(this);
		// Robot.actuel = new Spykee("192.168.1.15", UClient.PORT);

		// Hide title and notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set the main view
		setContentView(R.layout.main);

		// Set axes to the joystick view
		Joystick joystick = (Joystick) findViewById(R.id.joystick);
		joystick.setAxes(Robot.actuel.getAxes());

		// Initialize the first robot camera
		Camera camera = Robot.actuel.getCameras()[0];
		camera.addHandler(new ImageHandler() {
			public void handle(InputStream bitmap) {
				// Post a new image
				MainActivity.this.bitmap = bitmap;
				imageHandler.post(imageDisplayer);
			}
		});
		camera.start();
    }
    
	@Override
	protected void onStop() {
		super.onStop();
		Robot.actuel.getCameras()[0].stop();
	}
    
    
}
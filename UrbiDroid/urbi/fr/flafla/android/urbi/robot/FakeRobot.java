/**
 * 
 */
package fr.flafla.android.urbi.robot;

import java.io.InputStream;

import android.content.Context;
import android.widget.Toast;
import fr.flafla.android.urbi.R;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Axes.Axis;
import fr.flafla.android.urbi.control.Camera;

/**
 * Faux robot utile pour le développement. Les actions sont affichées à l'écran
 * et une image enregistrée est passée en boucle.
 * 
 * @author merlin
 * 
 */
public class FakeRobot extends Robot {

	protected class FakeCamera extends Camera {

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		@Override
		protected InputStream getImage() {
			return context.getResources().openRawResource(R.raw.test);
		}

	}

	private Toast toast;
	private final Context context;

	public FakeRobot(String ip, int port, Context context) {
		super(ip, port);
		this.axes = new Axes[] {
				new Axes(null, new Axis(-100, 100)), new Axes(null, new Axis(-100, 100))
		};
		this.cameras = new Camera[] {
			new FakeCamera()
		};

		this.context = context;
	}
	
	@Override
	public void move() {
		// Get axes value
		int trackL = axes[0].y.value;
		int trackR = axes[1].y.value;

		// Toast values
		if (toast == null)
			toast = Toast.makeText(context, "go(" + trackL + ", " + trackR
					+ ")", Toast.LENGTH_SHORT);
		else
			toast.setText("go(" + trackL + ", " + trackR + ")");
		toast.show();
	}

}

/**
 * 
 */
package fr.flafla.android.urbi.robot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import fr.flafla.android.urbi.R;

/**
 * Faux robot utile pour le développement. Les actions sont affichées à l'écran
 * et une image enregistrée est passée en boucle.
 * 
 * @author merlin
 * 
 */
public class FakeRobot extends Robot {

	private Toast toast;
	private final Context context;

	public FakeRobot(Context context) {
		this.context = context;
	}
	
	@Override
	public void go(int trackL, int trackR) {
		if (toast == null)
			toast = Toast.makeText(context, "go(" + trackL + ", " + trackR
					+ ")", Toast.LENGTH_SHORT);
		else
			toast.setText("go(" + trackL + ", " + trackR + ")");
		toast.show();
	}

	@Override
	protected Bitmap getImage() {
		InputStream stream = context.getResources().openRawResource(R.raw.test);
		try {
			byte[] buffer = new byte[320*240*3];
			int nb = stream.read(buffer);
			Bitmap bitmap = Spykee.decodeBitmap(buffer, nb);
			return bitmap;
		} catch (FileNotFoundException e) {
			Log.e(getClass().getSimpleName(), "Impossible d'ouvrir le fichier", e);
			throw new RobotException("Impossible d'ouvrir le fichier", e);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Impossible de lire le fichier", e);
			throw new RobotException("Impossible de lire le fichier", e);
		}
	}

	@Override
	protected void initCamera() {
	}
}

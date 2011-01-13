/**
 * 
 */
package fr.flafla.android.urbi.robot;


import fr.flafla.android.urbi.UClient;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Classe abstraite d'accès aux fonctionnalité d'un robot géré par urbi
 * 
 * @author merlin
 *
 */
public abstract class Robot extends UClient {
	public static interface ImageHandler {
		public void handle(Bitmap bitmap);
	}
	
	/**
	 * Le robot géré par le système (un seul à la fois)
	 */
	public static Robot actuel;

	protected AsyncTask<Void, Bitmap, Boolean> thread;
	
	protected boolean stop = false;
	
	protected Robot() {
	}

	public abstract void go(Context context, int trackL, int trackR);
	
	
	protected abstract void initCamera();
	
	public void acquire(final Context context, final ImageHandler handler) {
		initCamera();
		if (thread == null) {
			thread = new AsyncTask<Void, Bitmap, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.e(getClass().getSimpleName(), "Pb de thread", e);
					}
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							Log.e(getClass().getSimpleName(), "Pb de thread", e);
						}
						publishProgress(getImage(context));
					} while (!stop);

					return true;
				}

				@Override
				protected void onProgressUpdate(Bitmap... values) {
					handler.handle(values[0]);
				}
			};

		}
		
		thread.execute();
	}
	
	public void stopAcquire(Context context) {
		stop = true;
	}
	
	protected abstract Bitmap getImage(final Context context);
}

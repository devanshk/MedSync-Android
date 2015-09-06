package pennapps.rxconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * Created by devanshk on 10/3/14.
 */
public class DelayTask extends AsyncTask<Void,Integer,Void> {
    private final String TAG = "Async_LoadSoul";
    private long delay;
    private Runnable runnable;
    private Activity parent;

    public DelayTask(Activity Parent, long delay, Runnable runnable){
        this.parent = Parent;
        this.delay = delay;
        this.runnable = runnable;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{Thread.sleep(delay);} catch(Exception e){}
        parent.runOnUiThread(runnable);
        return null;
    }

    protected void onPostExecute(Void v){
    }
}

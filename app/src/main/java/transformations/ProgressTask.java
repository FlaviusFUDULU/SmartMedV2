package transformations;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by ffudulu on 08-Feb-17.
 */
public class ProgressTask extends AsyncTask <Void,Void,Void> {

    private ProgressBar bar;

    public ProgressTask(ProgressBar bar) {
        this.bar = bar;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        bar.setVisibility(View.GONE);
    }
}

package pennapps.rxconnect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Created by devanshk on 9/5/15.
 */
public class MedFragment extends Fragment {
    static CircleRefreshLayout mRefreshLayout;
    public static ArrayList<CustomExpandableView> medViews = new ArrayList<CustomExpandableView>();
    static LinearLayout mLinearLayout;

    public MedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meds, container, false);

        mRefreshLayout = (CircleRefreshLayout)v.findViewById(R.id.refresh_layout);
        mLinearLayout = (LinearLayout)v.findViewById(R.id.linear_layout);

        mRefreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {
                System.out.println("Completed Refresh.");
            }

            @Override
            public void refreshing() {
                populateData();
            }
        });

        for (int i=0;i<4;i++){
            mLinearLayout.addView(generatePrescriptionView("Drugs","Take them","Be safe",0));
        }

        return v;
    }

    View generatePrescriptionView(String medication, String directions, String doctorNote, Integer refills){
        CustomExpandableView v = new CustomExpandableView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(15, 20, 15, 0);
        v.setLayoutParams(lp);
        v.setBackgroundResource(R.color.primary);
        v.fillData(0, medication, true);

        View i = View.inflate(getActivity(),R.layout.item_directions,null);
        ((TextView)i.findViewById(R.id.slot)).setText(directions);
        v.addContentView(i);
        i = View.inflate(getActivity(),R.layout.item_doctorsnote,null);
        ((TextView)i.findViewById(R.id.slot)).setText(doctorNote);
        v.addContentView(i);

        v.setTag(R.id.ExpandID, UUID.randomUUID());
        medViews.add(v);

        return v;
    }

    void clearMedViews(){
        for (View v: medViews)
            ((ViewGroup)v.getParent()).removeView(v);
        medViews.clear();
    }

    void populateData(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://45.79.141.245:3000/api/patient/55eab073c280131934217955.json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                System.out.println("Response="+new String(response));
                DelayTask dt = new DelayTask(getActivity(), 1000, new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Done sleeping. Finishing Refresh.");
                        MedFragment.mRefreshLayout.finishRefreshing();
                    }
                });

                dt.executeOnExecutor(Executors.newSingleThreadExecutor());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                MedFragment.mRefreshLayout.finishRefreshing();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}

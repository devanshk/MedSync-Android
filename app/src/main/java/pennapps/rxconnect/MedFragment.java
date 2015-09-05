package pennapps.rxconnect;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import org.apache.http.Header;

/**
 * Created by devanshk on 9/5/15.
 */
public class MedFragment extends Fragment {
    static CircleRefreshLayout mRefreshLayout;

    public MedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meds, container, false);

        mRefreshLayout = (CircleRefreshLayout)v.findViewById(R.id.refresh_layout);
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

        return v;
    }

    View generatePrescriptionView(String medication, String directions, String doctorNote){
        View v = new CustomExpandableView(getActivity());
        return v;
    }

    void populateData(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.google.com", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                System.out.println("Response="+response);
                try{ Thread.sleep(1000);} catch(Exception e){e.printStackTrace();}
                System.out.println("Done sleeping. Finishing Refresh.");
                MedFragment.mRefreshLayout.finishRefreshing();
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

package pennapps.rxconnect;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by devanshk on 9/5/15.
 */
public class MedFragment extends Fragment {
    static CircleRefreshLayout mRefreshLayout;
    public static ArrayList<CustomExpandableView> medViews = new ArrayList<CustomExpandableView>();
    static LinearLayout mLinearLayout;

    public static AlertDialog mAlertDialog;
    public static AlertDialog.Builder builder;

    static String data; //Dirty hackathon code. Don't do this.
    String curMed;
    String curInstructions;
    long curTime;

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

        for (int i=0;i<1;i++){
            mLinearLayout.addView(generatePrescriptionView("Tylenol","Take them","Be safe",0));
        }

        return v;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    View generatePrescriptionView(String medication, String directions, String doctorNote, Integer refills){
        CustomExpandableView v = new CustomExpandableView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(15, 20, 15, 0);
        v.setLayoutParams(lp);
        v.setBackgroundResource(R.color.primary);
        v.fillData(ManUtils.medIcon(medication), medication, true);

        v.setElevation(2.5f);

        View i = View.inflate(getActivity(), R.layout.item_directions,null);
        ((TextView)i.findViewById(R.id.slot)).setText(directions);
        v.addContentView(i);
        i = View.inflate(getActivity(),R.layout.item_doctorsnote,null);
        ((TextView)i.findViewById(R.id.slot)).setText(doctorNote);
        v.addContentView(i);

        i = View.inflate(getActivity(),R.layout.item_reminders,null);
        View j = i.findViewById(R.id.button_background);
        j.setTag(R.id.Med,medication);
        j.setTag(R.id.Instructions,directions);
        j.setTag(R.id.Activated, false);
        j.setTag(R.id.TextView,i.findViewById(R.id.remind_text));
        j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView remindText = (TextView)v.getTag(R.id.TextView);
                Boolean activated = (Boolean)v.getTag(R.id.Activated);
                if (activated){
                    remindText.setText("Don't Remind Me");
                    ((ImageView)v).setImageResource(R.drawable.alarm_inactive);
                    v.setTag(R.id.Activated,false);
                }
                else {
                    remindText.setText("Remind Me");
                    ((ImageView)v).setImageResource(R.drawable.alarm_active);
                    v.setTag(R.id.Activated, true);
                    long tar = parseTimeFromDescription("" + v.getTag(R.id.Instructions));
                    if (tar > 0)
                        createReminder("" + v.getTag(R.id.Med), "" + v.getTag(R.id.Instructions), tar);
                }
            }
        });
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
        client.get("http://45.79.141.245:3000/api/patient/55eb89afd4ab099a65f8d61a.json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                // called when response HTTP status is "200 OK"
                System.out.println("Response=" + new String(response));
                DelayTask dt = new DelayTask(getActivity(), 1000, new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Done sleeping. Finishing Refresh.");
                        parseData(new String(response));
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

    void parseData(String d){
        data = d;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearMedViews();

                String desc = "1 Pill with water at 05:16 daily";

                mLinearLayout.addView(
                        generatePrescriptionView("Claritin", desc, "Do not take immediately after Tylenol.", 3)
                );

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    System.out.println("jsonObject=" + jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray("prescriptions");
                    System.out.println("jsonArray=" + jsonArray);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        System.out.println("jo=" + jo);
                        String med = jo.getString("medication");
                        String direc = jo.getString("directions");
                        Integer refills = jo.getInt("refills");
                        String doctorNote = jo.getString("doctor_notes");
                        mLinearLayout.addView(
                                generatePrescriptionView(med, direc, doctorNote, refills));
                    }

                    //Test Alerts


                /*Intent i = new Intent(getActivity(),MainActivity.class);
                i.putExtra("MedicineName","Tylenol");
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 5, i, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pendingIntent); */

                    DelayTask d = new DelayTask(getActivity(), 750, new Runnable() {
                        @Override
                        public void run() {
                            if (medViews.size() > 0)
                                medViews.get(0).expand();
                        }
                    });
                    d.executeOnExecutor(Executors.newSingleThreadExecutor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    long parseTimeFromDescription(String s){
        //1 Pill with water at 11:17pm daily
        try {
            Integer hour = Integer.parseInt(
                    s.substring(s.indexOf(":") - 2, s.indexOf(":")));
            Integer minutes = Integer.parseInt(
                    s.substring(s.indexOf(":") + 1, s.indexOf(":") + 3));
            Date d = new Date();
            d.setHours(hour);
            d.setMinutes(minutes);
            d.setSeconds(0);

            return d.getTime();
        } catch(Exception e){return 0;}
    }

    void createReminder(String med, final String instructions, long tarTime){
        View dialogView = View.inflate(getActivity(), R.layout.dialog_alert, null);
        ImageView icon = (ImageView) dialogView.findViewById(R.id.medicine_icon);
        TextView title = (TextView) dialogView.findViewById(R.id.medicine_title);
        TextView subtitle = (TextView) dialogView.findViewById(R.id.medicine_instructions);
        View okay = dialogView.findViewById(R.id.alert_okay);
        View snooze = dialogView.findViewById(R.id.alert_snooze);

        icon.setImageResource(ManUtils.medIcon(med));

        snooze.setTag(R.id.Med, med);
        snooze.setTag(R.id.Instructions, instructions);

        title.setText(med);
        subtitle.setText(instructions);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                createReminder("" + v.getTag(R.id.Med), "" + v.getTag(R.id.Instructions), System.currentTimeMillis() + 2500);
            }
        });

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        DelayTask delayTask = new DelayTask(getActivity(), tarTime - System.currentTimeMillis(), new Runnable() {
            @Override
            public void run() {
                mAlertDialog = builder.show();
            }
        });
        delayTask.executeOnExecutor(Executors.newSingleThreadExecutor());

        System.out.println("Reminder Created.");
    }
}

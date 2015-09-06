package pennapps.rxconnect;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    Context context;
    List<Contact> listContact;

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        listContact = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = View.inflate(context, R.layout.item_contact, null);

        int drawableId = -1;

        Contact contact = listContact.get(position);

        try {
            Class res = R.drawable.class;
            Field field = res.getField("p" + Integer.toString(contact.pic));
            drawableId = field.getInt(null);
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
        }

        ((TextView)convertView.findViewById(R.id.name)).setText(contact.name);
        ((TextView)convertView.findViewById(R.id.field1)).setText(contact.field1);
        ((TextView)convertView.findViewById(R.id.field2)).setText(contact.field2);
        ((ImageView)convertView.findViewById(R.id.pic)).setImageResource(drawableId);

        if (position!=0){
            convertView.findViewById(R.id.accent).setVisibility(View.GONE);
        }
        if (position==1)
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call("3037791367");
                }
            });

        else
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call("3133120792");
                }
            });

        return convertView;
    }

    private void call(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            UserCardsFragment.parent.startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("fail", "Call failed", e);
        }
    }
}

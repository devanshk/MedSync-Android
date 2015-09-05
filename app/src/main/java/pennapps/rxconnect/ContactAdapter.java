package pennapps.rxconnect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Dominic on 9/5/2015.
 */
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
        return convertView;
    }
}

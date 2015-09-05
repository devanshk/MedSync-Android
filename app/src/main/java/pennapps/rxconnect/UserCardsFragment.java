package pennapps.rxconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class UserCardsFragment extends Fragment {

    public UserCardsFragment() {
    }

    private ListView lstContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usercards, container, false);

        lstContacts = (ListView) v.findViewById(R.id.lstContacts);

        lstContacts.setDivider(null);

        ArrayList<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("Dom", "field1", "field2"));
        contacts.add(new Contact("Chris", "field1", "field2"));
        contacts.add(new Contact("Harry", "field1", "field2"));



        lstContacts.setAdapter(new ContactAdapter(getActivity(), R.layout.item_contact, contacts));

        return v;
    }
}

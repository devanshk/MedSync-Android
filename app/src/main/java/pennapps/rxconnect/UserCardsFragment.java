package pennapps.rxconnect;

import android.app.Activity;
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
    public static ArrayList<Contact> contacts = new ArrayList<Contact>();
    public static Activity parent;

    public UserCardsFragment() {
    }

    private ListView lstContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usercards, container, false);
        parent = getActivity();

        lstContacts = (ListView) v.findViewById(R.id.lstContacts);

        if(!ManUtils.populatedContacts) {
            ManUtils.populatedContacts=true;
            lstContacts.setDivider(null);

            contacts.add(new Contact("Wilma Johnson", "Female", "70 years old", 1));
            contacts.add(new Contact("Dr.Rosalyn Espinoza, MD", "Family Physician", "Beaumont Hospital", 2));
            contacts.add(new Contact("Jon Li, PharmD", "Pharmacist", "CVS Pharmacy", 3));
        }

        lstContacts.setAdapter(new ContactAdapter(getActivity(), R.layout.item_contact, contacts));

        return v;
    }
}

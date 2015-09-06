package pennapps.rxconnect;

import java.util.ArrayList;

/**
 * Created by devanshk on 9/5/15.
 */
public class ManUtils {
    public static boolean populatedContacts=false;

    static ArrayList<String> pills = new ArrayList<String>(){{
        add("Tylenol");
        add("Advil");
        add("Benadryl");
    }};

    static ArrayList<String> syrups = new ArrayList<String>(){{
        add("Mucinex");
    }};

    static ArrayList<String> tablets = new ArrayList<String>(){{
        add("Penicillin");
        add("Claritin");
    }};

    public static int medIcon(String name){
        for (String s : pills){
            if (name.equals(s))
                return R.drawable.pill_icon;
        }
        for (String s: syrups){
            if (name.equals(s))
                return R.drawable.syrup_icon;
        }
        for (String s : tablets){
            if (name.equals(s))
                return R.drawable.tablet_icon;
        }

        return R.drawable.pill_icon;
    }
}

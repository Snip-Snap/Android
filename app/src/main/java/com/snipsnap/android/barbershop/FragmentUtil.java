package com.snipsnap.android.barbershop;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public final class FragmentUtil {
    private static final FragmentUtil obj = new FragmentUtil();

    public static FragmentUtil getObj() {
        return obj;
    }
    public void addFragment(Fragment fragment,
                            FragmentManager fragmentManager, int container) {
        FragmentTransaction frag_tran = fragmentManager.beginTransaction();
        frag_tran.add(container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

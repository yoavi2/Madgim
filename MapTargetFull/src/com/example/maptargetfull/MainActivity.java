package com.example.maptargetfull;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AbstractNavDrawerActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState == null ) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FirstFragment()).commit();
        }
    }
    
    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        
        NavDrawerItem[] menu = new NavDrawerItem[] {
                NavMenuSection.createMenuSection(100, R.string.text_mapsection),        
                NavMenuItem.createMenuItem(101, R.string.text_map, R.drawable.google_icon, true, false), 
                NavMenuItem.createMenuItem(102, R.string.text_map, R.drawable.ic_action_locate, true, false),
                NavMenuItem.createMenuItem(103, R.string.text_list, R.drawable.ic_action_paste, true, false)
 };
        
        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);       
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setDrawerIcon(R.drawable.ic_drawer);
        navDrawerActivityConfiguration.setAdapter(
            new NavDrawerAdapter(this, R.layout.navdrawer_item, menu));
        return navDrawerActivityConfiguration;
    }
    
    @Override
    protected void onNavItemSelected(int id) {
        switch (id) {
        case 101:
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new GoogleMapFragment(), GoogleMapFragment.TAG).commit();
            break;
        case 102:
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FirstFragment()).commit();
            break;
        case 103:
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SecondFragment()).commit();

        }
    }
    
    static public class MainFragment extends Fragment {

    	public static String TAG = "main_fragment";
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View mainView = inflater
                    .inflate(R.layout.fragment_main, container, false);

            return mainView;
        }
    }
    
}

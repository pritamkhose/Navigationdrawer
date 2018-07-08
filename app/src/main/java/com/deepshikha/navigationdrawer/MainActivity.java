package com.deepshikha.navigationdrawer;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Model_country> al_main = new ArrayList<>();
    ExpandableListView ev_list;
    CountryAdapter obj_adapter;
    String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    HomeFragment fragment;
    TextView tv_name;
    RelativeLayout rl_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fn_data();
        init();

    }

    private void init() {

        getSupportActionBar().hide();
        ev_list = (ExpandableListView) findViewById(R.id.ev_menu);
        tv_name = (TextView) findViewById(R.id.tv_name);
        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        obj_adapter = new CountryAdapter(MainActivity.this, al_main);
        ev_list.setAdapter(obj_adapter);
        ev_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

        setExpandableListViewHeightBasedOnChildren(ev_list);

        fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", al_main.get(0).getStr_country());
        bundle.putString("des", al_main.get(0).getAl_state().get(0).getStr_description());
        bundle.putString("dish", al_main.get(0).getAl_state().get(0).getStr_name());
        bundle.putString("image", al_main.get(0).getAl_state().get(0).getStr_image());
        tv_name.setText(al_main.get(0).getStr_country());

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "HomeFragment").addToBackStack("null").commit();


        rl_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
       /* if (height < 10)
            height = 200;*/
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void fn_data() {

        String str_data = loadJSONFromAsset();

        try {
            JSONObject jsonObject_country = new JSONObject(str_data);
            JSONArray jsonArray_country = jsonObject_country.getJSONArray("country");
            al_main = new ArrayList<>();
            for (int i = 0; i < jsonArray_country.length(); i++) {
                Model_country obj_country = new Model_country();
                JSONObject jsonObject = jsonArray_country.getJSONObject(i);
                JSONArray jsonArray_dishes = jsonObject.getJSONArray("dishes");
                ArrayList<Model_Dish> al_dishes = new ArrayList<>();
                for (int j = 0; j < jsonArray_dishes.length(); j++) {

                    JSONObject jsonObject_dishes = jsonArray_dishes.getJSONObject(j);
                    Model_Dish obj_dish = new Model_Dish();
                    obj_dish.setStr_name(jsonObject_dishes.getString("dishname"));
                    obj_dish.setStr_description(jsonObject_dishes.getString("description"));
                    obj_dish.setStr_image(jsonObject_dishes.getString("image"));
                    al_dishes.add(obj_dish);
                }

                obj_country.setAl_state(al_dishes);
                obj_country.setStr_country(jsonObject.getString("name"));

                al_main.add(obj_country);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setExpandableListViewHeightBasedOnChildren(ExpandableListView expandableListView) {
        CountryAdapter adapter = (CountryAdapter) expandableListView.getExpandableListAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = expandableListView.getPaddingTop() + expandableListView.getPaddingBottom();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            View groupItem = adapter.getGroupView(i, false, null, expandableListView);
            groupItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (expandableListView.isGroupExpanded(i)) {
                for (int j = 0; j < adapter.getChildrenCount(i); j++) {
                    View listItem = adapter.getChildView(i, j, false, null, expandableListView);
                    listItem.setLayoutParams(new ViewGroup.LayoutParams(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED));
                    listItem.measure(View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        int height = totalHeight + expandableListView.getDividerHeight() * (adapter.getGroupCount() - 1);

        if (height < 10)
            height = 100;
        params.height = height;
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("dishes.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        Log.e(TAG, "Json response " + json);
        return json;

    }

    public void fn_selectedPosition(int group, int child) {

        fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", al_main.get(group).getStr_country());
        bundle.putString("des", al_main.get(group).getAl_state().get(child).getStr_description());
        bundle.putString("dish", al_main.get(group).getAl_state().get(child).getStr_name());
        bundle.putString("image", al_main.get(group).getAl_state().get(child).getStr_image());
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "HomeFragment").addToBackStack("null").commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);

        tv_name.setText(al_main.get(group).getStr_country());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

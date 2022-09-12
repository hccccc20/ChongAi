package com.example.chongai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chongai.fragment.OwnerFragment;
import com.example.chongai.fragment.RecommendFragment;
import com.example.chongai.fragment.ToolFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        fragmentList.add(new RecommendFragment());
        fragmentList.add(new ToolFragment());
        fragmentList.add(new OwnerFragment());

        viewPager2.setUserInputEnabled(true);//设置能否滑动
   //     viewPager2.setOffscreenPageLimit(3);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewPager2.setAdapter(new HomePagerAdapter(this, fragmentList));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_recommend:
                        viewPager2.setCurrentItem(0);
                        return true;
                    case R.id.navigation_tool:
                        viewPager2.setCurrentItem(1);
                        return true;
                    case R.id.navigation_owner:
                        viewPager2.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }

    public static class HomePagerAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

    }
}
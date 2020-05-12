package com.example.picdog.ui.main.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.picdog.R
import com.example.picdog.ui.main.view.MainFragment

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
  FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  private val tabTitles = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4
  )


  override fun getItem(position: Int): Fragment {
    return MainFragment.newInstance(position + 1)
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return context.resources.getString(tabTitles[position])
  }

  override fun getCount(): Int {
    return 4
  }
}
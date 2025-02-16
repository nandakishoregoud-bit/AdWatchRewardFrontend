package com.example.myandroidapp.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myandroidapp.fragments.Top10DayFragment
import com.example.myandroidapp.fragments.Top10WeekFragment
import com.example.myandroidapp.fragments.Top10MonthFragment

class PopularQuestionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Top10DayFragment()
            1 -> Top10WeekFragment()
            2 -> Top10MonthFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}

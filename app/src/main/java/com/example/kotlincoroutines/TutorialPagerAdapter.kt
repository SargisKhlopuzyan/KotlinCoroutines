package com.example.kotlincoroutines

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlincoroutines.model.Tutorial

/**
 * Created by FastShift, Inc., on 12/2/2019.
 *
 * @author Sargis Khlopuzyan (sargis.khlopuzyan@fcc.am)
 */
class TutorialPagerAdapter(
    private val tutorialList: List<Tutorial>,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return tutorialList.size
    }

    override fun getItem(position: Int): Fragment {
//        return TutorialSuspendFragment.newInstance(tutorialList[position])
        return TutorialFragment.newInstance(tutorialList[position])
    }
}
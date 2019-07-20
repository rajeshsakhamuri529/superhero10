package com.blobcity.activity


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class test4 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(DashBoardActivity::class.java)

    @Test
    fun test4() {
        val recyclerView = onView(
            allOf(
                withId(R.id.rcv_chapter),
                childAtPosition(
                    withClassName(`is`("android.widget.RelativeLayout")),
                    3
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        val appCompatButton = onView(
            allOf(
                withId(R.id.btn_quiz2), withText("Quiz II"),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_quiz_2),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btn_start), withText("start"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    9
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val webView = onView(
            allOf(
                withId(R.id.webView_option3),
                childAtPosition(
                    allOf(
                        withId(R.id.wv_4100_ll),
                        childAtPosition(
                            withId(R.id.ll_quiz),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )

        webView.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.btn_next), withText("NEXT"),
                childAtPosition(
                    allOf(
                        withId(R.id.rl_hint_next),
                        childAtPosition(
                            withId(R.id.rl_root),
                            4
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

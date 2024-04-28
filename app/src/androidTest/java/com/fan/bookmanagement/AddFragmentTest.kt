package com.fan.bookmanagement

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fan.bookmanagement.fragments.AddFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddFragmentTest {

    @Test
    fun should_show_title_and_edit_texts_and_button() {
        launchFragmentInContainer<AddFragment>(themeResId = R.style.Theme_BookManagement)
        onView(withId(R.id.textview_fragment_add_title)).check(matches(withText(R.string.fragment_add_title)))
        onView(withId(R.id.edittext_title)).check(matches(withHint(R.string.book_title_hint)))
        onView(withId(R.id.edittext_author)).check(matches(withHint(R.string.book_author_hint)))
        onView(withId(R.id.edittext_year)).check(matches(withHint(R.string.publication_year_hint)))
        onView(withId(R.id.edittext_isbn)).check(matches(withHint(R.string.isbn_hint)))
        onView(withId(R.id.button_add)).check(matches(withText(R.string.button_add_text)))
    }

}
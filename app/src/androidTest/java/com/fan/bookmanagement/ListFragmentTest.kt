import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fan.bookmanagement.R
import com.fan.bookmanagement.fragments.ListFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ListFragmentTest {

    @Test
    fun should_show_recycler_view() {
        launchFragmentInContainer<ListFragment>(themeResId = R.style.Theme_BookManagement)
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun should_navigate_to_add_fragment_when_clicking_floating_add_button() {
        val mockNavController = mock(NavController::class.java)

        val scenario =
            launchFragmentInContainer<ListFragment>(themeResId = R.style.Theme_BookManagement)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        onView(withId(R.id.floating_add_button)).perform(click())

        verify(mockNavController).navigate(R.id.action_fragment_list_to_fragment_add)
    }
}



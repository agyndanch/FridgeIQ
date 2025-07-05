package com.bignerdranch.android.fridgeiq

import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bignerdranch.android.fridgeiq.ui.adapter.FoodItemAdapter
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FridgeIQTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    // ===========================================
    // NAVIGATION TESTS
    // ===========================================

    @Test
    fun testBottomNavigationInventoryTab() {
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        onView(withId(R.id.recycler_view_inventory))
            .check(matches(isDisplayed()))

        onView(withId(R.id.fab_add_food))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBottomNavigationAnalyticsTab() {
        onView(withId(R.id.navigation_analytics))
            .perform(click())

        onView(withId(R.id.text_monthly_cost))
            .check(matches(isDisplayed()))

        onView(withId(R.id.text_weekly_count))
            .check(matches(isDisplayed()))

        onView(withId(R.id.chart_waste_by_category))
            .check(matches(isDisplayed()))
    }

    // ===========================================
    // FOOD ITEM MANAGEMENT TESTS
    // ===========================================

    @Test
    fun testAddNewFoodItemViaMenAction() {
        // Navigate to inventory
        onView(withId(R.id.navigation_inventory)).perform(click())

        // Click FAB to add food item
        onView(withId(R.id.fab_add_food)).perform(click())

        // Verify we're on the add food screen
        onView(withId(R.id.edit_text_name))
            .check(matches(isDisplayed()))

        // Fill out form
        onView(withId(R.id.edit_text_name))
            .perform(typeText("Organic Milk"))
            .perform(closeSoftKeyboard())

        // Select category (assuming Dairy is at position 0)
        onView(withId(R.id.spinner_category))
            .perform(click())
        onView(withText("Dairy"))
            .perform(click())

        // Set quantity
        onView(withId(R.id.edit_text_quantity))
            .perform(clearText(), typeText("2"))
            .perform(closeSoftKeyboard())

        // Select unit
        onView(withId(R.id.spinner_unit))
            .perform(click())
        onView(withText("bottle"))
            .perform(click())

        // Select storage location
        onView(withId(R.id.spinner_storage_location))
            .perform(click())
        onView(withText("Refrigerator"))
            .perform(click())

        // Set expiration date (click on date field)
        onView(withId(R.id.text_expiration_date))
            .perform(click())

        // Select today's date in date picker (simplified)
        onView(withText("OK"))
            .perform(click())

        // Set cost
        onView(withId(R.id.edit_text_cost))
            .perform(typeText("4.99"))
            .perform(closeSoftKeyboard())

        // Save the item
        onView(withId(R.id.action_save))
            .perform(click())

        // Verify we're back on inventory screen
        onView(withId(R.id.recycler_view_inventory))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEditExistingFoodItem() {
        // Add an item first
        addTestFoodItem("Edit Me", "Dairy")

        // Click on the item to edit
        onView(withId(R.id.recycler_view_inventory))
            .perform(actionOnItemAtPosition<FoodItemAdapter.FoodItemViewHolder>(0,
                click()))

        // Modify the name
        onView(withId(R.id.edit_text_name))
            .perform(clearText(), typeText("Edited Item"))
            .perform(closeSoftKeyboard())

        // Save changes
        onView(withId(R.id.action_save))
            .perform(click())

        // Verify the change appears in the list
        onView(withText("Edited Item"))
            .check(matches(isDisplayed()))
    }

    // ===========================================
    // SHOPPING LIST TESTS
    // ===========================================

    @Test
    fun testAddNewShoppingListItem() {
        // Navigate to shopping list
        onView(withId(R.id.navigation_shopping_list))
            .perform(click())

        // Click FAB to add item
        onView(withId(R.id.fab_add_item))
            .perform(click())

        // Fill out form
        onView(withId(R.id.edit_text_name))
            .perform(typeText("Bread"))
            .perform(closeSoftKeyboard())

        // Select category
        onView(withId(R.id.spinner_category))
            .perform(click())
        onView(withText("Pantry"))
            .perform(click())

        // Set quantity
        onView(withId(R.id.edit_text_quantity))
            .perform(clearText(), typeText("2"))
            .perform(closeSoftKeyboard())

        // Select unit
        onView(withId(R.id.spinner_unit))
            .perform(click())
        onView(withText("piece"))
            .perform(click())

        // Save
        onView(withId(R.id.action_save))
            .perform(click())

        // Verify item appears in shopping list
        onView(withText("Bread"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCheckOffShoppingListItem() {
        // Navigate to shopping list
        onView(withId(R.id.navigation_shopping_list))
            .perform(click())

        // Add an item first
        addTestShoppingItem("Milk", "Dairy")

        // Check the item as purchased
        onView(withId(R.id.checkbox_purchased))
            .perform(click())

        // Verify checkbox is checked
        onView(withId(R.id.checkbox_purchased))
            .check(matches(isChecked()))
    }

    @Test
    fun testOpenBarcodeScanner() {
        // Navigate to inventory
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        // Click scan barcode menu item
        onView(withId(R.id.action_scan_barcode))
            .check(matches(instanceOf(ActionMenuItemView::class.java)))
            .perform(click())

        // Verify scanner screen elements
        onView(withId(R.id.viewFinder))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_manual_entry))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBarcodeScannerManualEntry() {
        // Navigate to inventory
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        // Open scanner
        onView(withId(R.id.action_scan_barcode))
            .perform(click())

        // Click manual entry
        onView(withId(R.id.button_manual_entry))
            .perform(click())

        // Should navigate back to add food form
        onView(withId(R.id.edit_text_name))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBarcodeScannerCameraIntent() {
        Intents.init()

        // Navigate to inventory
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        // Open scanner - this should request camera permission
        // In a real test, you'd need to grant camera permission
        onView(withId(R.id.action_scan_barcode))
            .perform(click())

        // Verify camera preview is set up
        onView(withId(R.id.viewFinder))
            .check(matches(isDisplayed()))

        Intents.release()
    }

    // ===========================================
    // FILTER TESTS
    // ===========================================

    @Test
    fun testInventoryFilterDialog() {
        // Navigate to inventory
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        // Open filter dialog
        onView(withId(R.id.action_filter))
            .perform(click())

        // Verify filter dialog elements
        onView(withId(R.id.spinner_filter_category))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.spinner_filter_location))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.checkbox_expiring_only))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testClearFilters() {
        // Navigate to inventory and apply a filter first
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        onView(withId(R.id.action_filter))
            .perform(click())

        // Clear filters
        onView(withText("Clear"))
            .inRoot(isDialog())
            .perform(click())

        // All items should be visible again
    }

    // ===========================================
    // ANALYTICS TESTS
    // ===========================================

    @Test
    fun testAnalyticsDataDisplay() {
        // Navigate to analytics
        onView(withId(R.id.navigation_analytics))
            .perform(click())

        // Verify stats are displayed
        onView(withId(R.id.text_monthly_cost))
            .check(matches(isDisplayed()))

        onView(withId(R.id.text_weekly_count))
            .check(matches(isDisplayed()))

        // Verify chart is present
        onView(withId(R.id.chart_waste_by_category))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testInventorySwipeRefresh() {
        // Navigate to inventory
        onView(withId(R.id.navigation_inventory))
            .perform(click())

        // Perform swipe to refresh
        onView(withId(R.id.swipe_refresh))
            .perform(swipeDown())

        // Should trigger refresh (hard to test without actual data changes)
        onView(withId(R.id.recycler_view_inventory))
            .check(matches(isDisplayed()))
    }

    // ===========================================
    // HELPER METHODS
    // ===========================================

    private fun addTestFoodItem(name: String, category: String) {
        onView(withId(R.id.navigation_inventory)).perform(click())
        onView(withId(R.id.fab_add_food)).perform(click())

        onView(withId(R.id.edit_text_name))
            .perform(typeText(name))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.spinner_category))
            .perform(click())
        onView(withText(category))
            .perform(click())

        onView(withId(R.id.action_save))
            .perform(click())
    }

    private fun addTestShoppingItem(name: String, category: String) {
        onView(withId(R.id.fab_add_item)).perform(click())

        onView(withId(R.id.edit_text_name))
            .perform(typeText(name))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.spinner_category))
            .perform(click())
        onView(withText(category))
            .perform(click())

        onView(withId(R.id.action_save))
            .perform(click())
    }
}
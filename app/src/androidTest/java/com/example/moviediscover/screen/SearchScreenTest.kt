package com.example.moviediscover.screen

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.example.moviediscover.data.SortCriteria
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBar_performSearch_callsOnSearch() {
        println("Running searchBar_performSearch_callsOnSearch()...")
        var searchCalled = false

        composeTestRule.setContent {
            MovieSearchBar(
                query = "initial",
                onQueryChange = {},
                onSearch = {
                    searchCalled = true
                }
            )
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Batman")
        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        println("searchCalled = $searchCalled")
        assert(searchCalled)
    }

    @Test
    fun sortCriteriaMenu_clickItem_callsCallbackAndDismisses() {
        println("sortCriteriaMenu_clickItem_callsCallbackAndDismisses()...")
        var clickedCriteria: SortCriteria? = null
        var dismissed = false

        composeTestRule.setContent {
            SortCriteriaMenu(
                isVisible = true,
                onSortCriteriaClick = { clickedCriteria = it },
                onToggleSortCriteria = { dismissed = true }
            )
        }

        val firstCriteria = SortCriteria.entries.first()
        composeTestRule
            .onNodeWithText(firstCriteria.displayName)
            .performClick()

        println("clickedCriteria == firstCriteria = ${clickedCriteria == firstCriteria}")
        println("dismissed = $dismissed")
        assert(clickedCriteria == firstCriteria)
        assert(dismissed)
    }
}
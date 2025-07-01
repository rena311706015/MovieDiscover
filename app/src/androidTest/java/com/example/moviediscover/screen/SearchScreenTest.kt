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

        // 模擬輸入文字
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Batman")

        // 模擬按下搜尋（鍵盤上 Enter）
        composeTestRule.onNode(hasSetTextAction()).performImeAction()

        // 驗證 searchCalled 被設為 true
        assert(searchCalled)
    }

    @Test
    fun sortCriteriaMenu_clickItem_callsCallbackAndDismisses() {
        var clickedCriteria: SortCriteria? = null
        var dismissed = false

        composeTestRule.setContent {
            SortCriteriaMenu(
                isVisible = true,
                onSortCriteriaClick = { clickedCriteria = it },
                onToggleSortCriteria = { dismissed = true }
            )
        }

        // 找到第一個排序選項並點擊
        val firstCriteria = SortCriteria.entries.first()
        composeTestRule
            .onNodeWithText(firstCriteria.displayName)
            .performClick()

        // 驗證 callback 是否有被呼叫
        assert(clickedCriteria == firstCriteria)
        assert(dismissed)
    }
}
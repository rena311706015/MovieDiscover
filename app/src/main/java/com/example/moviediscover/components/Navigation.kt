package com.example.moviediscover.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moviediscover.R
import com.example.moviediscover.data.Screen

class NavItem(
    val route: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
) {
    companion object {
        fun getNavItems() = listOf(
            NavItem(Screen.Home.route, R.drawable.home_filled, R.drawable.home_border),
            NavItem(Screen.Search.route, R.drawable.search_border, R.drawable.search_border),
            NavItem(Screen.Bookmark.route, R.drawable.bookmark_filled, R.drawable.bookmark_border),
        )
    }
}

@Composable
fun BottomNavigation(
    navController: NavController,
) {
    val bottomNavItems = NavItem.getNavItems()
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        for (screen in bottomNavItems) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon),
                        tint = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute == screen.route) return@NavigationBarItem
                    navController.navigate(screen.route)
                    {
                        popUpTo(screen.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationRail(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.NavigationRail(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val railNavItems = NavItem.getNavItems()
            for (screen in railNavItems) {
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon),
                            tint = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = { navController.navigate(screen.route) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
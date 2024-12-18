package com.jayanthr.spendisense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jayanthr.spendisense.feature.add_expense.AddExpense
import com.jayanthr.spendisense.feature.home.HomeScreen
import com.jayanthr.spendisense.feature.stats.StatsScreen
import com.jayanthr.spendisense.feature.transaction_list.TransactionList
import com.jayanthr.spendisense.ui.theme.zinc

@Composable
fun NavHostScreen(){

    val navController = rememberNavController()
    var bottomBarVisibility by remember { mutableStateOf(true) }

    Scaffold (bottomBar = {
        AnimatedVisibility(visible = bottomBarVisibility) {
            NavigationBottomBar(
                navController = navController,
                items = listOf(
                    NavItem(route = "/home", icon = R.drawable.ic_home),
                    NavItem(route = "/stats", icon = R.drawable.ic_stats),
                    NavItem(route = "/transaction", icon = R.drawable.ic_rupee),
                    NavItem(route = "/addExpense", icon = R.drawable.ic_user)

                )
            )
        }
    }){
        NavHost(navController = navController, startDestination = "/home",
            modifier = Modifier.padding(it)){
        composable(route = "/home"){
            bottomBarVisibility = true
            HomeScreen(navController)
        }

        composable(route = "/addExpense"){
            bottomBarVisibility = false
            AddExpense(navController)
        }


         composable(route = "/transaction"){
          bottomBarVisibility = true
          TransactionList(navController)
          }


        composable(route = "/stats"){
            bottomBarVisibility = true
            StatsScreen(navController)
        }
    }
    } }





data class NavItem(
    val route: String,
    val icon: Int
)

@Composable
fun NavigationBottomBar(
    navController: NavController,
    items: List<NavItem>
){
    //BOTTOM NAV BAR
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach{ item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = item.icon),
                        contentDescription = null
                        )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = zinc,
                    selectedTextColor =  zinc,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview
@Composable
fun Preview(){
    NavHostScreen()
}
package com.jayanthr.spendisense.feature.transaction_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jayanthr.spendisense.R
import com.jayanthr.spendisense.data.model.ExpenseEntity
import com.jayanthr.spendisense.feature.home.TransactionItem
import com.jayanthr.spendisense.viewmodel.HomeViewModel
import com.jayanthr.spendisense.viewmodel.HomeViewModelFactory
import com.jayanthr.spendisense.widget.ExpenseTextView

@Composable
fun TransactionList(navController: NavController) {
    val viewModel : HomeViewModel =
        HomeViewModelFactory(LocalContext.current)
            .create(HomeViewModel::class.java)
    val state = viewModel.expenses.collectAsState(initial = emptyList())

    Surface(modifier = Modifier.fillMaxSize()) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, wrapper, topBar, add) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.namebar_homepage),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Box(modifier = Modifier.fillMaxWidth()
                .constrainAs(nameRow){

                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                }
                .padding(24.dp)
            )
             {

                Image(
                    painter = painterResource(R.drawable.chevron_left),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable{
                            navController.navigate("/home")
                        },

                    contentDescription = null,
                )



                ExpenseTextView(
                    "Transaction List",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 64.dp)

                )

            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .shadow(shape = RectangleShape, elevation = 24.dp)
                .constrainAs(wrapper){
                    bottom.linkTo(parent.bottom)
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints

                }
                .clip(RoundedCornerShape(60.dp))
                .background(Color.White)



            ){

            }

            TransactionView(
                modifier = Modifier.fillMaxSize().constrainAs(list) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints

                }
                    .padding(top = 48.dp),
                list = state.value, viewModel = viewModel, navController = navController
            )

        }
    }
}


@Composable
fun TransactionView(modifier: Modifier, list : List<ExpenseEntity>, viewModel: HomeViewModel, navController: NavController){
    LazyColumn (modifier = modifier.padding(24.dp)) {

        items(list) { item ->

                TransactionItem(
                    title = item.title,
                    amount = item.amount.toString(),
                    icon = viewModel.getItemIcon(item),
                    date = item.date,
                    color = if(item.type == "Income") Color.Green else Color.Red ,
                )
            }

        }

    }




@Preview(showBackground = true)
@Composable
fun TransactionListPreview(){
    TransactionList(rememberNavController())
}
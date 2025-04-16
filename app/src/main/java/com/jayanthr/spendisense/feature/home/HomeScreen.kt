package com.jayanthr.spendisense.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.jayanthr.spendisense.ui.theme.zinc
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jayanthr.spendisense.R
import com.jayanthr.spendisense.data.model.ExpenseEntity
import com.jayanthr.spendisense.viewmodel.HomeViewModel
import com.jayanthr.spendisense.viewmodel.HomeViewModelFactory
import com.jayanthr.spendisense.widget.ExpenseTextView


@Composable
fun HomeScreen(navController: NavController){
    val viewModel : HomeViewModel =
        HomeViewModelFactory(LocalContext.current)
            .create(HomeViewModel::class.java)

    Surface (modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar, add) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.namebar_homepage),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                Column {
                    ExpenseTextView(text = "Good Afternoon!", fontSize = 16.sp, color = Color.White)
                    ExpenseTextView(text = "Jayanth", fontSize = 20.sp, fontWeight = FontWeight.Bold , color = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)

                )
            }

            val state = viewModel.expenses.collectAsState(initial = emptyList())
            val expenses = viewModel.getTotalExpense(state.value)
            val income = viewModel.getTotalIncome(state.value)
            val balance = viewModel.getBalance(state.value)

            CardItem(
                modifier = Modifier
                    .constrainAs(card) {
                        top.linkTo(nameRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, balance = balance, income = income, expenses = expenses,
            )

            TransactionList(
                modifier = Modifier.fillMaxSize().constrainAs(list) {
                    top.linkTo(card.bottom)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints

                },
                list = state.value, viewModel = viewModel, navController = navController
            )
            Image(painter = painterResource(R.drawable.ic_add), contentDescription = null ,
                modifier = Modifier
                    .constrainAs(add){
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable{
                        navController.navigate("/addExpense")
                    }
            )
        }
    }
}

@Composable
fun CardItem(modifier: Modifier, balance: String, income: String, expenses: String){
    Column ( modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()
        .height(200.dp)
        .shadow(shape = RectangleShape, elevation = 20.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(zinc)
        .padding(16.dp)

    ) {
        Box( modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Column (modifier = Modifier.align(Alignment.CenterStart)) {
                ExpenseTextView(text = "Total's Spending", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.ExtraLight)
                ExpenseTextView(
                    text = expenses,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Image( painter = painterResource(id = R.drawable.dotsmenu_homepage),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Box (modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            ){
            CardRowItem(
                modifier = Modifier.align(Alignment.CenterStart),
                title = "Remaining Allowance",
                amount = income,
                image = R.drawable.ic_rupee,
            )

            CardRowItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Total Balance",
                amount = balance,
                image = R.drawable.ic_rupee,
            )
        }

    }
}

@Composable
fun TransactionList(modifier: Modifier, list: List<ExpenseEntity>, viewModel: HomeViewModel, navController: NavController){
    LazyColumn (modifier = modifier.padding(16.dp)){
        item {
            Box(modifier = Modifier.fillMaxWidth()){
                ExpenseTextView( text = "Recent Transaction",
                    fontSize = 20.sp)
                ExpenseTextView(
                    text = "See All",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .clickable{
                            navController.navigate("/transaction")
                        }

                )
            }
        }
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

@Composable
fun CardRowItem(modifier: Modifier, title: String, amount: String, image: Int){
    Column( modifier = modifier) {
        Row {
            Image( painter = painterResource( id = image), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(5.dp))
            ExpenseTextView(
                text = title,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding( top = 4.dp)
            )
        }
        ExpenseTextView( text = amount,
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding( start = 7.dp),


        )
    }
}

@Composable
fun TransactionItem(title:String, amount: String, icon:Int, date: String, color: Color){
Box(modifier = Modifier.fillMaxWidth().padding( vertical = 8.dp))
{
    Row {
        Image(
            painter = painterResource(id = icon), contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))


        Column {
            ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            ExpenseTextView(text = date, fontSize = 16.sp, color = Color.Gray)
        }
    }
    ExpenseTextView( text = amount ,
        fontSize = 20.sp ,
        color = color,
        modifier = Modifier.align(Alignment.CenterEnd),
        fontWeight = FontWeight.SemiBold
    )
}

}

@Composable
@Preview (showBackground = true)
fun PreviewHomeScreen(){
    HomeScreen(rememberNavController())
}
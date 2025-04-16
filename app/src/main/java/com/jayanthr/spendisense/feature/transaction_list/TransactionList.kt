package com.jayanthr.spendisense.feature.transaction_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    val viewModel: HomeViewModel =
        HomeViewModelFactory(LocalContext.current)
            .create(HomeViewModel::class.java)
    val state = viewModel.expenses.collectAsState(initial = emptyList())
    val expenses = viewModel.getTotalExpense(state.value)
    val income = viewModel.getTotalIncome(state.value)
    var selectedOption = remember { mutableStateOf("Expense") }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, wrapper, topBar) = createRefs()

            // Top bar
            Image(
                painter = painterResource(id = R.drawable.namebar_homepage),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.chevron_left),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.navigate("/home") },
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

            // Background Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(shape = RectangleShape, elevation = 24.dp)
                    .constrainAs(wrapper) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(nameRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ExpenseTextView(
                            text = if (selectedOption.value == "Expense") "Total Expenses" else "Total Income",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Row for expense amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ExpenseTextView(
                            text = if (selectedOption.value == "Expense") expenses else income,
                            fontSize = 28.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp, start = 6.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ExpenseIncomeToggle(selectedOption.value) { option ->
                            selectedOption.value = option
                        }
                    }
                }
            }

            TransactionView(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(list) {
                        top.linkTo(wrapper.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .padding(top = 80.dp),
                list = state.value, viewModel = viewModel, selectedOption = selectedOption.value
            )
        }
    }
}


@Composable
fun ExpenseIncomeToggle(selectedOption: String, onOptionSelected: (String) -> Unit) {
    val options = listOf("Expense", "Income")

    Row(
        modifier = Modifier
            .shadow(6.dp, shape = RoundedCornerShape(12.dp)) // Adds a drop shadow
            .background(Color.White, shape = RoundedCornerShape(12.dp)) // Background color
            ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .background(
                        if (selectedOption == option) Color.Green else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 14.dp)
                    .width(150.dp),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (selectedOption == option) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionView(modifier: Modifier, list : List<ExpenseEntity>, viewModel: HomeViewModel, selectedOption: String){
    LazyColumn(
        modifier = modifier.padding(
            top = 110.dp,
            start = 24.dp,
            end = 24.dp,
            bottom = 24.dp
        )
    ) {

        items(list) { item ->
            if (item.type == selectedOption) {
                TransactionItem(
                    title = item.title,
                    amount = item.amount.toString(),
                    icon = viewModel.getItemIcon(item),
                    date = item.date,
                    color = if (item.type == "Income") Color.Green else Color.Red,
                )
            }
        }
    }

    }




@Preview(showBackground = true)
@Composable
fun TransactionListPreview(){
    TransactionList(rememberNavController())
}
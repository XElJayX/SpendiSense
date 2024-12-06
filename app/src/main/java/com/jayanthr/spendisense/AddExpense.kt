package com.jayanthr.spendisense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.jayanthr.spendisense.widget.ExpenseTextView


@Composable
fun AddExpense(){
    Surface (modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.namebar_homepage),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
                .constrainAs(nameRow){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ){
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                ExpenseTextView(
                    text = "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
                Image(
                    painter = painterResource(id = R.drawable.dotsmenu_homepage),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            DataForm(modifier = Modifier
                .padding(top = 60.dp)
                .constrainAs(card){
                top.linkTo(nameRow.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

            })
        }
    }
}

@Composable
fun DataForm(modifier: Modifier){

    Column (modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()
        .shadow(16.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .padding(16.dp)
        .verticalScroll(rememberScrollState())

    ){
        ExpenseTextView( text = "Type",
            fontSize = 14.sp,
            color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value="",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(4.dp))

        ExpenseTextView( text = "Name",
            fontSize = 14.sp,
            color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value="",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))

        ExpenseTextView( text = "Category",
            fontSize = 14.sp,
            color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value="",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))

        ExpenseTextView( text = "Amount",
            fontSize = 14.sp,
            color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value="",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        ExpenseTextView( text = "Date",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.size(4.dp))

        OutlinedTextField(value="",
            onValueChange = {},
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))

        Button(onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp)))
        {
            ExpenseTextView( text = "Add Expense",
                fontSize = 14.sp,
                color = Color.White
                )
        }
    }
}

@Composable
@Preview
fun AddExpensePreview(){
    AddExpense()
}
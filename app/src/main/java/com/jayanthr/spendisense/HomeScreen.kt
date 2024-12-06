package com.jayanthr.spendisense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.constraintlayout.compose.Dimension


@Composable
fun HomeScreen(){
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
                    Text(text = "Good Afternoon!", fontSize = 16.sp, color = Color.White)
                    Text(text = "Jayanth", fontSize = 20.sp, fontWeight = FontWeight.Bold , color = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)

                )
            }
            CardItem(modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

            TransactionList(modifier = Modifier.fillMaxSize().constrainAs(list){
              top.linkTo(card.bottom)
              start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
              end.linkTo(parent.end)
                height = Dimension.fillToConstraints

            })


        }
    }
}

@Composable
fun CardItem( modifier: Modifier){
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
                Text(text = "Total's Spending", fontSize = 16.sp, color = Color.White)
                Text(
                    text = "₹ 25",
                    fontSize = 20.sp,
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
                amount = "₹ 21",
                image = R.drawable.ic_rupee,
            )

            CardRowItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Monthly Goal",
                amount = "₹ 5000",
                image = R.drawable.ic_rupee,
            )
        }

    }
}

@Composable
fun TransactionList(modifier: Modifier){
    Column (modifier = modifier.padding(16.dp)){
        Box(modifier = Modifier.fillMaxWidth()){
            Text( text = "Recent Transaction",
                fontSize = 20.sp)
            Text(
                text = "See All",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )

        }
        TransactionItem(
            title = "Shopping",
            amount = "- $25",
            icon = R.drawable.ic_notifications,
            date = "Today",
            color = Color.Red,
        )

    }

}

@Composable
fun CardRowItem(modifier: Modifier, title: String, amount: String, image: Int){
    Column( modifier = modifier) {
        Row {
            Image( painter = painterResource( id = image), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding( top = 4.dp)
            )
        }
        Text( text = amount,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding( start = 7.dp)

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
            Text(text = title, fontSize = 16.sp)
            Text(text = date, fontSize = 16.sp)
        }
    }
    Text( text = amount , fontSize = 20.sp , color = color, modifier = Modifier.align(Alignment.CenterEnd))
}

}

@Composable
@Preview (showBackground = true)
fun PreviewHomeScreen(){
    HomeScreen()
}
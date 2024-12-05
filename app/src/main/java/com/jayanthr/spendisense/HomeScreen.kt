package com.jayanthr.spendisense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                    painter = painterResource(id = R.drawable.dotsmenu_homepage),
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
            Row {
                Column {
                    Text(text = "Total Balance", fontSize = 16.sp, color = Color.White)
                    Text(
                        text = "INR 25,000",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
    }
}

@Composable
@Preview (showBackground = true)
fun PreviewHomeScreen(){
    HomeScreen()
}
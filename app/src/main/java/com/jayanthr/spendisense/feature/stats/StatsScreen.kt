package com.jayanthr.spendisense.feature.stats

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.jayanthr.spendisense.R
import com.jayanthr.spendisense.Utils.Utils
import com.jayanthr.spendisense.viewmodel.StatsViewModel
import com.jayanthr.spendisense.viewmodel.StatsViewModelFactory
import com.jayanthr.spendisense.widget.ExpenseTextView
import java.util.*

@Composable
fun StatsScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF8F9FA)
    val accentColor = Color(0xFF2F7E79)
    val secondaryColor = Color(0xFF6C63FF)
    val textColorPrimary = Color(0xFF333333)
    val textColorSecondary = Color(0xFF666666)

    var selectedPeriod by remember { mutableStateOf("Monthly") }
    var showChartOptions by remember { mutableStateOf(false) }
    var selectedChartType by remember { mutableStateOf("Line") }

    val viewModel = StatsViewModelFactory(navController.context)
        .create(StatsViewModel::class.java)
    val dataState = viewModel.entries.collectAsState(emptyList())
    val entries = viewModel.getEntriesForChart(dataState.value)

    // Expense summary data
    val totalSpent = "₹${viewModel.getTotalAmount(dataState.value)}"
    val avgPerDay = "₹${viewModel.getAveragePerDay(dataState.value)}"
    val mostExpensiveDay = viewModel.getMostExpensiveDay(dataState.value)

    // Categories data
    val categoryData = viewModel.getCategoryBreakdown(dataState.value)

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (topBarBackground, topBarContent, periodSelector, chartOptionsRow, mainContent) = createRefs()

            // Top bar background image
            Image(
                painter = painterResource(id = R.drawable.namebar_homepage),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBarBackground) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Top bar content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(topBarContent) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        .size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )

                ExpenseTextView(
                    text = "Expense Statistics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                Image(
                    painter = painterResource(id = R.drawable.dotsmenu_homepage),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { showChartOptions = !showChartOptions }
                        .size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            // Time period selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .constrainAs(periodSelector) {
                        top.linkTo(topBarContent.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val periods = listOf("Weekly", "Monthly", "Yearly")
                periods.forEach { period ->
                    val isSelected = period == selectedPeriod
                    val buttonColor = animateColorAsState(
                        targetValue = if (isSelected) accentColor else Color.White
                    )
                    val textColor = animateColorAsState(
                        targetValue = if (isSelected) Color.White else textColorSecondary
                    )

                    Button(
                        onClick = { selectedPeriod = period },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor.value
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = period,
                            color = textColor.value,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Chart type dropdown menu
            AnimatedVisibility(
                visible = showChartOptions,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.constrainAs(chartOptionsRow) {
                    top.linkTo(periodSelector.bottom)
                    end.linkTo(parent.end)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    val chartTypes = listOf("Line", "Bar", "Pie")
                    chartTypes.forEach { type ->
                        val isSelected = type == selectedChartType
                        Text(
                            text = type,
                            color = if (isSelected) accentColor else textColorSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable {
                                    selectedChartType = type
                                    showChartOptions = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Main content with charts and stats
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .constrainAs(mainContent) {
                        top.linkTo(
                            if (showChartOptions) chartOptionsRow.bottom else periodSelector.bottom,
                            margin = 8.dp
                        )
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .verticalScroll(rememberScrollState())
            ) {
                // Main chart section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        when (selectedChartType) {
                            "Line" -> LineChart(entries = entries, accentColor)
                            "Pie" -> PieChart(data = categoryData, accentColor)
                        }
                    }
                }

                // Expense summary cards
                Text(
                    text = "EXPENSE SUMMARY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorSecondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCard(
                        title = "Total",
                        value = totalSpent,
                        icon = R.drawable.ic_rupee,
                        color = accentColor,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    SummaryCard(
                        title = "Average/Day",
                        value = avgPerDay,
                        icon = R.drawable.ic_calendar,
                        color = secondaryColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Top spending categories
                if (categoryData.isNotEmpty()) {
                    Text(
                        text = "TOP CATEGORIES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        categoryData.take(3).forEach { (category, amount, percentage) ->
                            CategoryItem(
                                category = category,
                                amount = "₹$amount",
                                percentage = percentage
                            )
                        }
                    }
                }

                // Expense insights
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F8FF)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "INSIGHTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your highest spending day was $mostExpensiveDay",
                            fontSize = 14.sp,
                            color = textColorSecondary
                        )

                        if (categoryData.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "You spend most on ${categoryData.first().first}",
                                fontSize = 14.sp,
                                color = textColorSecondary
                            )
                        }
                    }
                }

                // Add some bottom padding for better scrolling experience
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: String,
    amount: String,
    percentage: Float
) {
    val progressAnimation = animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )

            Text(
                text = "$amount • ${percentage.toInt()}%",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressAnimation.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when {
                            percentage > 50 -> Color(0xFFE57373)
                            percentage > 25 -> Color(0xFFFFB74D)
                            else -> Color(0xFF81C784)
                        }
                    )
            )
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>, accentColor: Color) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            val view = LayoutInflater.from(context).inflate(R.layout.stats_line_chart, null)
            view
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { view ->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setDrawGridBackground(false)
        lineChart.setExtraOffsets(8f, 16f, 8f, 16f)

        val dataSet = LineDataSet(entries, "Expenses").apply {
            color = accentColor.hashCode()
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 2.5f
            setDrawCircles(true)
            setCircleColor(accentColor.hashCode())
            circleRadius = 4f
            setDrawCircleHole(true)
            circleHoleRadius = 2f
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawFilled(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 10f
            valueTextColor = android.graphics.Color.parseColor("#666666")
            val drawable = ContextCompat.getDrawable(context, R.drawable.char_gradient)
            drawable?.let {
                fillDrawable = it
            }
            setDrawValues(false)
            highlightLineWidth = 1.5f
        }

        lineChart.data = com.github.mikephil.charting.data.LineData(dataSet).apply {
            setValueTextSize(10f)
            setValueTypeface(Typeface.DEFAULT)
        }

        // X-Axis formatting
        lineChart.xAxis.apply {
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    return Utils.formatDateForChart(value.toLong())
                }
            }
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            textColor = android.graphics.Color.parseColor("#666666")
            setDrawGridLines(false)
            setDrawAxisLine(true)
            granularity = 1f
        }

        // Y-Axis formatting
        lineChart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = android.graphics.Color.parseColor("#EEEEEE")
            setDrawAxisLine(false)
            textColor = android.graphics.Color.parseColor("#666666")
            textSize = 10f
            setDrawZeroLine(true)
        }

        lineChart.axisRight.isEnabled = false

        // Legend formatting
        lineChart.legend.apply {
            form = Legend.LegendForm.LINE
            textSize = 12f
            textColor = android.graphics.Color.parseColor("#333333")
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
        }

        // Add animation
        lineChart.animateXY(1000, 1000)

        lineChart.invalidate()
    }
}

//@Composable
//fun BarChart(entries: List<Entry>, accentColor: Color) {
//    val context = LocalContext.current
//    AndroidView(
//        factory = {
//            val view = LayoutInflater.from(context).inflate(R.layout.stats_line_chart, null)
//            view
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) { view ->
//        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
//
//        // Convert entries to BarEntries
//        val barEntries = entries.map { BarEntry(it.x, it.y) }
//
//        val barDataSet = BarDataSet(barEntries, "Expenses").apply {
//            color = accentColor.hashCode()
//            valueTextColor = android.graphics.Color.parseColor("#666666")
//            valueTextSize = 10f
//            setDrawValues(false)
//        }
//
//        val barData = com.github.mikephil.charting.data.BarData(barDataSet).apply {
//            barWidth = 0.7f
//        }
//
//        // Clear any previous LineData
//        lineChart.clear()
//
//        // Set the bar data
//        b.data = barData
//
//        // Chart styling
//        lineChart.description.isEnabled = false
//        lineChart.setTouchEnabled(true)
//        lineChart.isDragEnabled = true
//        lineChart.setScaleEnabled(true)
//        lineChart.setPinchZoom(true)
//        lineChart.setDrawGridBackground(false)
//        lineChart.setExtraOffsets(8f, 16f, 8f, 16f)
//
//        // X-Axis formatting
//        lineChart.xAxis.apply {
//            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
//                override fun getFormattedValue(value: Float): String? {
//                    return Utils.formatDateForChart(value.toLong())
//                }
//            }
//            position = XAxis.XAxisPosition.BOTTOM
//            textSize = 10f
//            textColor = android.graphics.Color.parseColor("#666666")
//            setDrawGridLines(false)
//            setDrawAxisLine(true)
//            granularity = 1f
//        }
//
//        // Y-Axis formatting
//        lineChart.axisLeft.apply {
//            setDrawGridLines(true)
//            gridColor = android.graphics.Color.parseColor("#EEEEEE")
//            setDrawAxisLine(false)
//            textColor = android.graphics.Color.parseColor("#666666")
//            textSize = 10f
//            setDrawZeroLine(true)
//        }
//
//        lineChart.axisRight.isEnabled = false
//
//        // Legend formatting
//        lineChart.legend.apply {
//            form = Legend.LegendForm.SQUARE
//            textSize = 12f
//            textColor = android.graphics.Color.parseColor("#333333")
//            verticalAlignment = Legend.LegendVerticalAlignment.TOP
//            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//            orientation = Legend.LegendOrientation.HORIZONTAL
//            setDrawInside(false)
//        }
//
//        // Add animation
//        lineChart.animateY(1000)
//
//        lineChart.invalidate()
//    }
//}

@Composable
fun PieChart(data: List<Triple<String, Float, Float>>, accentColor: Color) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            val view = LayoutInflater.from(context).inflate(R.layout.stats_line_chart, null)
            view
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { view ->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        // Replace with PieChart
        val pieChart = PieChart(context)
        (view as? ViewGroup)?.apply {
            removeAllViews()
            addView(pieChart, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ))
        }

        // Create PieEntries
        val pieEntries = data.mapIndexed { index, (category, amount, _) ->
            PieEntry(amount, category)
        }

        val dataSet = PieDataSet(pieEntries, "Expense Categories").apply {
            // Generate rainbow colors based on number of entries
            val colors = mutableListOf<Int>()
            val baseColor = accentColor.hashCode()
            for (i in data.indices) {
                val hue = (i * 360f / data.size) % 360
                val hsv = FloatArray(3)
                android.graphics.Color.colorToHSV(baseColor, hsv)
                hsv[0] = hue
                colors.add(android.graphics.Color.HSVToColor(hsv))
            }
            setColors(colors)
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 12f
            sliceSpace = 3f
            selectionShift = 5f
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
        }

        pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(8f, 8f, 8f, 8f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(android.graphics.Color.WHITE)
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Expenses by\nCategory"
            setCenterTextSize(14f)
            setCenterTextColor(android.graphics.Color.parseColor("#333333"))
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            setEntryLabelColor(android.graphics.Color.WHITE)
            animateY(1000)

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textSize = 10f
                form = Legend.LegendForm.CIRCLE
            }

            // Add interaction listener
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    // Handle pie slice selection
                    e?.let {
                        if (it is PieEntry) {
                            centerText = "${it.label}\n${it.value}₹"
                        }
                    }
                }

                override fun onNothingSelected() {
                    centerText = "Expenses by\nCategory"
                }
            })
        }
    }
}

// Helper extensions
@Composable
fun animateColorAsState(targetValue: Color): State<Color> {
    return remember { mutableStateOf(targetValue) }.apply { value = targetValue }
}



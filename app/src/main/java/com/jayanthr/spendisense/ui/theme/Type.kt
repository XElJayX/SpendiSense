package com.jayanthr.spendisense.ui.theme
import com.jayanthr.spendisense.R

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


var InterFont = FontFamily(
    Font(R.font.inter_bold),
    Font(R.font.inter_thin),
    Font(R.font.inter_black),
    Font(R.font.inter_light),
    Font(R.font.inter_medium),
    Font(R.font.inter_extra_bold),
    Font(R.font.inter_extra_light),
    Font(R.font.inter_regular),
    Font(R.font.inter_semi_bold),

)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
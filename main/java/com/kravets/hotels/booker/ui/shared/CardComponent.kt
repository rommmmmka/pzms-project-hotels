package com.kravets.hotels.booker.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.kravets.hotels.booker.Config

@Composable
fun CardComponent(
    image: String? = null,
    title: String? = null,
    secondTitle: String? = null,
    thirdTitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .padding(bottom = 20.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        if (image != null) {
            CardImageBoxComponent(image, title, secondTitle, thirdTitle)
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 15.dp)
        ) {
            content.invoke()
        }
    }
}

@Composable
fun CardImageBoxComponent(
    image: String,
    title: String? = null,
    secondTitle: String? = null,
    thirdTitle: String? = null,
    paddingBottom: Dp = 0.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .padding(bottom = paddingBottom)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(Color.Yellow)
        )
        SubcomposeAsyncImage(
            model = "${Config.baseUrl}uploads/$image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp)),
            loading = {
                CircularProgressIndicator()
            },
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xEE000000)
                        ),
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(30.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                if (title != null) {
                    Text(
                        text = title,
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        ),
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (secondTitle != null) {
                    Text(
                        text = secondTitle,
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    )
                }
                if (thirdTitle != null) {
                    Text(
                        text = thirdTitle,
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CardHeaderComponent(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    )
}

@Composable
fun CardTextComponent(text: String, color: Color = Color.Black) {
    Text(
        text = text,
        style = TextStyle(color = color, fontSize = 15.sp)
    )
}

@Composable

fun CardTextBoldComponent(text: String, color: Color = Color.Black) {
    Text(
        text = text,
        style = TextStyle(
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    )
}
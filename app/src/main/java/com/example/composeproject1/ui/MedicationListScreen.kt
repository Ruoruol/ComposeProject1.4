package com.example.composeproject1.ui

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composeproject1.App
import com.example.composeproject1.database.MedicationData
import com.example.composeproject1.ui.theme.DeleteColor
import com.example.composeproject1.ui.theme.InvalidColor
import com.example.composeproject1.ui.theme.PrimaryColor
import com.example.composeproject1.viewmodel.MedicationListEvent
import java.sql.Date

@Composable
fun MedicationListScreen(
    dataList: List<MedicationData>,
    invalidList: List<MedicationData>,
    onEvent: (MedicationListEvent) -> Unit
) {
    LazyColumn(Modifier.fillMaxWidth()) {

        items(1) {
            Text(text = "等待通知:", modifier = Modifier.padding(top = 14.dp), fontSize = 30.sp)
        }
        items(dataList.size) {
            MedicationItem(
                modifier = Modifier.padding(top = if (it == 0) 0.dp else 5.dp),
                false,
                dataList[it],
                onEvent
            )

        }
        items(1) {
            Text(text = "過期通知:", modifier = Modifier.padding(top = 14.dp), fontSize = 30.sp)
        }
        items(invalidList.size) {
            MedicationItem(
                modifier = Modifier.padding(top = if (it == 0) 0.dp else 5.dp),
                true,
                invalidList[it],
                onEvent
            )
        }
    }
}

@Composable
fun MedicationItem(
    modifier: Modifier,
    isInvalid: Boolean,
    medicationData: MedicationData,
    onEvent: (MedicationListEvent) -> Unit
) {
    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .run {
                    if (isInvalid) {
                        background(color = InvalidColor)
                    } else {
                        background(color = PrimaryColor)
                    }
                }
                .padding(start = 14.dp, end = 14.dp)
        ) {
            val (title, desc, time, deleteButton) = createRefs()
            Text(medicationData.title, modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(desc.top)
            })
            Text(medicationData.description, modifier = Modifier.constrainAs(desc) {
                top.linkTo(title.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(time.bottom)
            })
            val timeString = remember(medicationData.time) {
                val date = Date(medicationData.time)
                val dateFormat = DateFormat.getLongDateFormat(App.appContext)
                val timeFormat = DateFormat.getTimeFormat(App.appContext)
                dateFormat.format(date) + " " + timeFormat.format(date)
            }
            Text(text = "時間：${timeString}", modifier = Modifier.constrainAs(time) {
                top.linkTo(desc.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            })
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeleteColor,
                    contentColor = Color.Black
                ), onClick = {
                    onEvent(MedicationListEvent.Delete(medicationData.id))
                }, modifier = Modifier.constrainAs(deleteButton) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }) {
                Text(text = "刪除", fontSize = 20.sp)
            }

        }
    }
}

@Preview
@Composable
fun MedicationListScreenPreview() {
    MedicationItem(Modifier,false, MedicationData(1, "1", "1", "1", "1", 1, 1)) {

    }

}
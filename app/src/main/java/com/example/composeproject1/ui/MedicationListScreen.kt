package com.example.composeproject1.ui

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composeproject1.App
import com.example.composeproject1.database.MedicationData
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
            Text(text = "等待通知:", modifier = Modifier.padding(top = 14.dp))
        }
        items(dataList.size) {
            MedicationItem(
                modifier = Modifier.padding(top = if (it == 0) 0.dp else 5.dp),
                dataList[it],
                onEvent
            )

        }
        items(1) {
            Text(text = "过期通知:", modifier = Modifier.padding(top = 14.dp))
        }
        items(invalidList.size) {
            MedicationItem(
                modifier = Modifier.padding(top = if (it == 0) 0.dp else 5.dp),
                invalidList[it],
                onEvent
            )
        }
    }
}

@Composable
fun MedicationItem(
    modifier: Modifier,
    medicationData: MedicationData,
    onEvent: (MedicationListEvent) -> Unit
) {
    val isInvalid = remember(medicationData.id) {
        medicationData.isValid == 0 || medicationData.time < System.currentTimeMillis()
    }
    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.Gray)
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
            Text(text = "时间：${timeString}", modifier = Modifier.constrainAs(time) {
                top.linkTo(desc.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            })
            Button(onClick = {
                onEvent(MedicationListEvent.Delete(medicationData.id))
            }, modifier = Modifier.constrainAs(deleteButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }) {
                Text(text = "删除")
            }

        }
    }
}

@Preview
@Composable
fun MedicationListScreenPreview() {
    MedicationItem(Modifier,MedicationData(1, "1", "1", "1", "1", 1, 1)) {

    }

}
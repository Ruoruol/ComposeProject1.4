package com.example.composeproject1.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeproject1.R
import com.example.composeproject1.bean.AreaEntity
import com.example.composeproject1.bean.CityAreaBean
import com.example.composeproject1.viewmodel.MainEvent


@Composable
fun MainScreen(
    cityList: List<String>,
    curSelectCityName: String?,
    curSelectBean: AreaEntity?,
    areaList: List<AreaEntity>,
    curQuality: CityAreaBean? = null,
    onEvent: (MainEvent) -> Unit
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            CityText(modifier = Modifier.weight(1f), curSelectCityName, cityList, onEvent = onEvent)
            AreaText(modifier = Modifier.weight(1f), curSelectBean, areaList, onEvent = onEvent)
        }
        Column(horizontalAlignment=Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState()
        )){
            Column(
                Modifier
                    .fillMaxWidth()) {
                Spacer(modifier = Modifier.height(15.dp)) // 增加行距
                Text(text = "指標(AQI): ${curQuality?.aqi}", color = Color.Black, fontSize = 36.sp, modifier = Modifier.padding(start = 24.dp))
                Spacer(modifier = Modifier.height(8.dp)) // 增加行距
                Text(text = "狀態: ${curQuality?.status}", color = Color.Black, fontSize = 34.sp, modifier = Modifier.padding(start = 24.dp))
                Spacer(modifier = Modifier.height(8.dp)) // 增加行距
                Text(text = "${curQuality?.publishtime}", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(start = 24.dp))
            }
            val quality = curQuality
            if (quality != null) {
                Spacer(modifier = Modifier.height(30.dp))
                val value = quality.aqi?.toIntOrNull() ?: 0
                val hint = if (value < 50) "詳細資料:空氣品質為良好，可正常戶外活動" else if (value > 50 && value <= 100) "詳細資料:極特殊敏感族群建議注意可能產生的咳嗽或呼吸急促症狀，但仍可正常戶外活動。" else if (value > 100 && value <= 150) "詳細資料:1.有心臟、呼吸道及心血管疾病患者、孩童及老年人，建議減少體力消耗活動及戶外活動，必要外出應配戴口罩。2.具有氣喘的人可能需增加使用吸入劑的頻率。" else "詳細資料:1.有心臟、呼吸道及心血管疾病患者、孩童及老年人，建議留在室內並減少體力消耗活動，必要外出應配戴口罩。2.具有氣喘的人可能需增加使用吸入劑的頻率。"
                Column(
                    Modifier
                        .fillMaxWidth()

                ) {


                    Text(text = hint,color = Color.Red,fontSize = 28.sp, modifier = Modifier.padding(start = 24.dp),lineHeight = 45.sp)
                    Spacer(modifier = Modifier.height(20.dp)) // 增加行距
                }
            }
            Button({
                onEvent(MainEvent.Refresh)
            }){

                Text("刷新", modifier = Modifier,fontSize = 28.sp)
            }
        }


    }

}

//选择城市
@Composable
fun CityText(
    modifier: Modifier,
    curSelectCityName: String?,
    cityList: List<String>,
    onEvent: (MainEvent) -> Unit
) {
    var isShowCityDropDown by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(start = 24.dp)
                .clickable {
                    isShowCityDropDown = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = curSelectCityName ?: "選擇縣市",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_triangle),
                contentDescription = null,
                modifier = Modifier
                    .width(12.dp)
                    .height(
                        6.dp
                    )
            )

        }
        AreaDropDown(isShow = isShowCityDropDown, dataList = cityList, convert = { it }, click = {
            onEvent(
                MainEvent.SelectCity(
                    it
                )
            )
        }, selectFunc = { it == curSelectCityName },
            onDismissRequest = {
                isShowCityDropDown = false
            })
    }

}

//選擇區域
@Composable
fun AreaText(
    modifier: Modifier,
    curSelectBean: AreaEntity?,
    areaList: List<AreaEntity>,
    onEvent: (MainEvent) -> Unit
) {
    var isShowAreaDropDown by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(start = 24.dp)
                .clickable {
                    isShowAreaDropDown = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = curSelectBean?.areaName ?: "選擇區域",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_triangle),
                contentDescription = null,
                modifier = Modifier
                    .width(12.dp)
                    .height(
                        6.dp
                    )
            )

        }
        AreaDropDown(
            isShow = isShowAreaDropDown,
            dataList = areaList,
            selectFunc = { it.areaId == curSelectBean?.areaId },
            convert = { it.areaName },
            click = {
                onEvent(
                    MainEvent.SelectArea(
                        it.areaId
                    )
                )
            },
            onDismissRequest = {
                isShowAreaDropDown = false
            })
    }

}

//下拉選單
@Composable
fun <T> AreaDropDown(
    isShow: Boolean,
    dataList: List<T>,
    convert: (T) -> String,
    selectFunc: (T) -> Boolean,
    click: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = isShow,
        onDismissRequest = { onDismissRequest.invoke() }) {
        Column(
            Modifier
                .height(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            dataList.forEach {
                Text(
                    text = convert(it),
                    color = if (selectFunc(it)) Color.Red else Color.Black,
                    modifier = Modifier
                        .defaultMinSize(200.dp)
                        .height(48.dp)
                        .clickable {
                            click(it)
                            onDismissRequest()
                        }
                        .wrapContentSize(),
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun MainPreview() {

}

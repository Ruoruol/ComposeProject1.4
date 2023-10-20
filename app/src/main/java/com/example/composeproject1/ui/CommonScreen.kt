package com.example.composeproject1.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composeproject1.bean.DrawerData
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.theme.ComposeProject1Theme
import com.example.composeproject1.ui.theme.PrimaryColor
import com.example.composeproject1.ui.theme.SelectColor
import kotlinx.coroutines.launch
import kotlin.io.path.fileVisitor

@Composable
fun NavigationDrawer(
    modifier: Modifier,
    stateChangedRequest: (Boolean) -> Unit,
) {
    Box {
        Column(
            modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFBB86FC))
            )
            Spacer(modifier = Modifier.height(14.dp))
            val list = remember {
                ResourceGlobalRepository.getDrawableDataList()
            }
            var selectIndex by remember {
                mutableIntStateOf(-1)
            }
            val context = LocalContext.current
            list.forEachIndexed { index, it ->
                DrawerItem(it, index == selectIndex) {
                    selectIndex = index
                    context.startActivity(Intent(context, it.targetClazz).apply {
                        if (it.flags != null) {
                            flags = it.flags
                        }
                    })
                    stateChangedRequest(false)
                }
            }
        }
        IconButton(
            onClick = {
                stateChangedRequest.invoke(false)
            },
            modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuOpen,
                contentDescription = "关闭",
                tint = Color.White
            )
        }

    }
}

@Composable
fun DrawerItem(drawerData: DrawerData, isSelect: Boolean, click: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                click.invoke()
            }
            .padding(start = 16.dp, end = 6.dp)
            .run { if (isSelect) background(SelectColor, CircleShape) else this }
            .padding(start = 8.dp)

    ) {
        IconButton(onClick = { click.invoke() }) {
            Icon(
                imageVector = drawerData.imageVector,
                contentDescription = "",
                tint = if (isSelect) PrimaryColor else Color.Black
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = drawerData.title,
            color = if (isSelect) PrimaryColor else Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeToolbar(title: String, clickBack: () -> Unit, clickAction: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, color = Color.Black) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = { clickBack.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                clickAction()
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description",
                    tint = Color.White
                )
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WeTemplateScreen(
    topTitle: String,
    clickBack: () -> Unit,
    content: @Composable (paddingTop: Dp) -> Unit
) {
    ComposeProject1Theme {
        val scaffoldState =
            rememberScaffoldState(
                drawerState = androidx.compose.material.rememberDrawerState(
                    DrawerValue.Closed,
                    confirmStateChange = {
                        true
                    }
                )
            )
        val scope = rememberCoroutineScope()

        androidx.compose.material.Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxWidth(),
            topBar = {
                WeToolbar(title = topTitle, {
                    clickBack()
                }) {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            },
            drawerContent = {
                NavigationDrawer(
                    modifier = Modifier,
                    stateChangedRequest = { show ->
                        scope.launch {
                            if (show) {
                                scaffoldState.drawerState.open()
                            } else {
                                scaffoldState.drawerState.close()
                            }
                        }
                    })
            }) {
            content(it.calculateTopPadding())
        }
    }

}

@Composable
@Preview
fun PreviewNavigationDrawer() {

}
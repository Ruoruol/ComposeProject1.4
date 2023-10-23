package com.example.composeproject1.ui

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeproject1.bean.DrawerData
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.theme.ComposeProject1Theme
import com.example.composeproject1.ui.theme.PrimaryColor
import com.example.composeproject1.ui.theme.SelectColor
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    modifier: Modifier,
    defaultIndex: Int = -1,
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
                    .background(PrimaryColor)
            )
            Spacer(modifier = Modifier.height(14.dp))
            val list = remember {
                ResourceGlobalRepository.getDrawableDataList()
            }
            var selectIndex by remember(defaultIndex) {
                mutableIntStateOf(defaultIndex)
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
                contentDescription = "關閉",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeToolbar(title: String, clickDrawShow: () -> Unit, clickLoginOut: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, color = Color.Black) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                clickLoginOut.invoke()
            }) {
                IconButton(onClick = { clickDrawShow.invoke() }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { clickLoginOut() }) {
                Text(text = "登出", color = Color.White, fontSize = 20.sp)
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = "Localized description",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun WeTemplateScreen(
    defaultIndex: Int = -1,
    drawerGesturesEnabled: Boolean = true,
    scaffoldState: ScaffoldState,
    topBarContent: @Composable () -> Unit,
    content: @Composable (paddingTop: Dp) -> Unit,
) {
    ComposeProject1Theme {
        val scope = rememberCoroutineScope()

        androidx.compose.material.Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxWidth(),
            topBar = {
                topBarContent()
            },
            drawerGesturesEnabled = drawerGesturesEnabled,
            drawerContent = {
                NavigationDrawer(
                    modifier = Modifier,
                    defaultIndex = defaultIndex,
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WeTemplateScreen(
    topTitle: String,
    defaultIndex: Int = -1,
    clickBack: () -> Unit,
    content: @Composable (paddingTop: Dp) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberScaffoldState(
            drawerState = androidx.compose.material.rememberDrawerState(
                DrawerValue.Closed,
                confirmStateChange = {
                    true
                }
            )
        )
    WeTemplateScreen(
        defaultIndex = defaultIndex,
        scaffoldState = scaffoldState,
        topBarContent = {
            WeToolbar(title = topTitle, {
                clickBack()
            }) {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        content = content
    )

}

@Composable
fun HomeTemplateScreen(
    topTitle: String,
    defaultIndex: Int = -1,
    clickLoginOut: () -> Unit,
    content: @Composable (paddingTop: Dp) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberScaffoldState(
            drawerState = androidx.compose.material.rememberDrawerState(
                DrawerValue.Closed,
                confirmStateChange = {
                    true
                }
            )
        )
    WeTemplateScreen(
        defaultIndex = defaultIndex,
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = false,
        topBarContent = {
            HomeToolbar(
                title = topTitle,
                clickDrawShow = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                clickLoginOut = {
                    clickLoginOut()
                }
            )
        },
        content = content
    )
}

@Composable
@Preview
fun PreviewNavigationDrawer() {

}
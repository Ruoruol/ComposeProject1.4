package com.example.composeproject1.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.theme.ComposeProject1Theme

@Composable
fun NavigationDrawer(
    modifier: Modifier,
    stateChangedRequest: (Boolean) -> Unit,
) {

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
        val context = LocalContext.current
        list.forEach {
            DrawerItem(text = it.title) {
                context.startActivity(Intent(context, it.targetClazz))
                stateChangedRequest(false)
            }
        }

    }
}

@Composable
fun DrawerItem(text: String, click: () -> Unit) {
    Text(text = text, color = Color.Black, modifier = Modifier
        .fillMaxWidth()
        .clickable {
            click.invoke()
        }
        .padding(start = 14.dp)
        .padding(vertical = 10.dp))
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
    var isShow by remember {
        mutableStateOf(false)
    }
    ComposeProject1Theme {
        val scaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = isShow) {
            if (isShow) {
                scaffoldState.drawerState.open()
            } else {
                scaffoldState.drawerState.close()
            }
        }
        androidx.compose.material.Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxWidth(),
            topBar = {
                WeToolbar(title = topTitle, {
                    clickBack()
                }) {
                    isShow = true
                }
            },
            drawerContent = {
                NavigationDrawer(
                    modifier = Modifier,
                    stateChangedRequest = { show ->
                        isShow = show
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
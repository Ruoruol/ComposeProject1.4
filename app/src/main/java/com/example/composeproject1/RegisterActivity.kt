package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.composeproject1.model.DatabaseRepository
import com.example.composeproject1.ui.theme.ComposeProject1Theme
import com.example.composeproject1.ui.theme.PrimaryColor
import com.example.composeproject1.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeProject1Theme {
                RegisterScreen()
            }
        }
    }

    @Composable
    fun RegisterScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "註冊", fontSize = 30.sp, color = Color.Black)
            var account by remember {
                mutableStateOf("")
            }

            AccountTextField(account) {
                account = it
            }
            Spacer(modifier = Modifier.height(5.dp))
            var password by remember {
                mutableStateOf("")
            }
            PasswordTextField(password) {
                password = it
            }
            Spacer(modifier = Modifier.height(5.dp))
            var confirmPassword by remember {
                mutableStateOf("")
            }
            PasswordConfirmTextField(confirmPassword) {
                confirmPassword = it
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    startRegister(account.trim(), password.trim(), confirmPassword.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(text = "註冊", fontSize = 20.sp)
            }
        }
    }

    private fun startRegister(account: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            ToastUtils.shortToast("兩次密碼不一致")
            return
        }
        if (password.isEmpty()) {
            ToastUtils.shortToast("密碼不能為空")
            return
        }
        if (account.isEmpty()) {
            ToastUtils.shortToast("帳號不能為空")
            return
        }
        lifecycleScope.launch {
            DatabaseRepository.hasUserAccount(account).let {
                if (it) {
                    withContext(Dispatchers.Main) {
                        ToastUtils.shortToast("帳號已存在")
                    }
                } else {
                    DatabaseRepository.insertUser(account, password)
                    withContext(Dispatchers.Main) {
                        ToastUtils.shortToast("註冊成功")
                        finish()
                    }
                }
            }
        }
    }

    @Composable
    fun AccountTextField(textContent: String, beforeTextChanged: (String) -> Unit) {
        TextField(value = textContent,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "帳號"
                )
            },
            placeholder = {
                Text(text = "請輸入帳號", fontSize = 20.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = PrimaryColor,
                focusedLeadingIconColor = PrimaryColor,
                focusedPlaceholderColor = PrimaryColor,
                unfocusedIndicatorColor = Color.Gray
            ),
            onValueChange = {
                beforeTextChanged(it)
            })
    }

    @Composable
    fun PasswordTextField(textContent: String, beforeTextChanged: (String) -> Unit) {
        TextField(
            value = textContent,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "密碼"
                )
            },
            placeholder = {
                Text(text = "請輸入密碼", fontSize = 20.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = PrimaryColor,
                focusedLeadingIconColor = PrimaryColor,
                focusedPlaceholderColor = PrimaryColor,
                unfocusedIndicatorColor = Color.Gray
            ),
            onValueChange = {
                beforeTextChanged(it)
            })
    }

    @Composable
    fun PasswordConfirmTextField(textContent: String, beforeTextChanged: (String) -> Unit) {
        TextField(value = textContent,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = "確認密碼"
                )
            },
            placeholder = {
                Text(text = "請再次輸入密碼", fontSize = 20.sp)
            },
            modifier = Modifier.fillMaxWidth(),

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = PrimaryColor,
                focusedLeadingIconColor = PrimaryColor,
                focusedPlaceholderColor = PrimaryColor,
                unfocusedIndicatorColor = Color.Gray
            ),
            onValueChange = {
                beforeTextChanged(it)
            })
    }

    @Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
    @Composable
    fun PreviewRegisterScreen() {
        RegisterScreen()
    }
}
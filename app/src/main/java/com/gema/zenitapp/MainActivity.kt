package com.gema.zenitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gema.zenitapp.ui.theme.ZenitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenitAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "signup") {

                    composable("signup") {
                        SignUpScreen(onNavigateToLogin = {
                            navController.navigate("login")
                        })
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToSignUp = { navController.navigate("signup") },
                            onLoginSuccess = { navController.navigate("home") }
                        )
                    }
                    composable("home") {
                        InicioScreen()
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    ZenitAppTheme {
        LoginScreen(onNavigateToSignUp = {}, onLoginSuccess = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    ZenitAppTheme {
        SignUpScreen(onNavigateToLogin = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    ZenitAppTheme {
        InicioScreen()
    }
}
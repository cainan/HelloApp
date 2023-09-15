package br.com.alura.helloapp.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import br.com.alura.helloapp.DestinosHelloApp
import br.com.alura.helloapp.preferences.PreferencesKey
import br.com.alura.helloapp.preferences.dataStore
import br.com.alura.helloapp.ui.login.FormularioLoginTela
import br.com.alura.helloapp.ui.login.FormularioLoginViewModel
import br.com.alura.helloapp.ui.login.LoginTela
import br.com.alura.helloapp.ui.login.LoginViewModel
import br.com.alura.helloapp.ui.navegaLimpo
import kotlinx.coroutines.launch

fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = DestinosHelloApp.Login.rota,
        route = DestinosHelloApp.LoginGraph.rota
    ) {
        composable(
            route = DestinosHelloApp.Login.rota,
        ) {
            val viewModel = hiltViewModel<LoginViewModel>()
            val state by viewModel.uiState.collectAsState()

            if (state.logado) {
                LaunchedEffect(Unit) {
                    navController.navegaLimpo(DestinosHelloApp.HomeGraph.rota)
                }
            }

            val dataStore = LocalContext.current.dataStore
            val scope = rememberCoroutineScope()

            LoginTela(
                state = state,
                onClickLogar = {
                    scope.launch {
                        dataStore.data.collect { settings ->
                            val user = settings[PreferencesKey.USER]
                            val password = settings[PreferencesKey.PASSWORD]

                            if (user == state.usuario && password == state.senha) {
                                dataStore.edit {
                                    it[booleanPreferencesKey("logado")] = true
                                }
                                viewModel.tentaLogar()
                            } else {
                                state.onErro(true)
                            }
                        }
                    }
                },
                onClickCriarLogin = {
                    navController.navigate(DestinosHelloApp.FormularioLogin.rota)
                }
            )
        }

        composable(
            route = DestinosHelloApp.FormularioLogin.rota,
        ) {
            val viewModel = hiltViewModel<FormularioLoginViewModel>()
            val state by viewModel.uiState.collectAsState()
            val dataStore = LocalContext.current.dataStore
            val scope = rememberCoroutineScope()

            FormularioLoginTela(
                state = state,
                onSalvar = {
                    scope.launch {
                        dataStore.edit { settings ->
                            settings[PreferencesKey.USER] = state.nome
                            settings[PreferencesKey.PASSWORD] = state.senha
                        }
                    }
                    navController.navegaLimpo(DestinosHelloApp.Login.rota)
                }
            )
        }
    }
}
package dev.develsinthedetails.eatpoopyoucat.feature.netPlay

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.develsinthedetails.eatpoopyoucat.core.ui.components.Scaffolds
import dev.develsinthedetails.eatpoopyoucat.core.ui.components.Spinner
import dev.develsinthedetails.eatpoopyoucat.core.ui.theme.AppTheme
import dev.develsinthedetails.eatpoopyoucat.core.utilities.NetworkUtils
import dev.develsinthedetails.eatpoopyoucat.core.utilities.valueOrEmpty
import dev.develsinthedetails.eatpoopyoucat.data.models.Player
import dev.develsinthedetails.eatpoopyoucat.feature.netPlay.services.Server
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid


@Composable
fun NetGameScreen(
    viewModel: JoinNetGameViewModel = koinViewModel(),
    gameId: Uuid,
    address: String
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var lanAddress by remember {
        mutableStateOf(
            NetworkUtils.getLocalIpAddress()?.let { "http://$it:3947" }
                .also { viewModel.updateAddress(it) }
                ?: "Server Offline")
    }
    val serverAction by viewModel.serverAction.collectAsState()

    LaunchedEffect(Unit) {
        val isWifiOn = NetworkUtils.isWifiConnected(context)
        val ipAddress = uiState.address
        viewModel.onStartServerRequested(isWifiOn, ipAddress)
    }

    LaunchedEffect(Unit) {
        while (true) {
            val ipAddress = NetworkUtils.getLocalIpAddress()
            lanAddress = if (ipAddress != null) {
                "http://$ipAddress:3947"
            } else {
                "Server Offline"
            }
            delay(1.seconds)
        }
    }

    LaunchedEffect(serverAction) {
        when (serverAction) {
            is StartNetGameViewModel.ServerAction.StartService -> {
                val serviceIntent = Intent(context, Server::class.java)
                context.startService(serviceIntent)
                viewModel.resetAction()
            }

            is StartNetGameViewModel.ServerAction.PromptWifiTurnOn -> {
                val panelIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent(Settings.Panel.ACTION_WIFI)
                } else {
                    Intent(Settings.ACTION_WIFI_SETTINGS)
                }
                context.startActivity(panelIntent)
                viewModel.resetAction()
            }

            StartNetGameViewModel.ServerAction.Idle -> { /* Do nothing */
            }
        }
    }
    viewModel.initFromDeepLink(gameId, address)
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Spinner()
        }
    } else {
        val joinData = JoinData(
            onChangeNickname = { newName -> viewModel.updateNickname(newName) },
            onYesPlay = { viewModel.onYesPlay() },
            onNoPlay = { (context as? Activity)?.finishAffinity() }
        )

        AskToJoinScreen(
            uiState,
            joinData = joinData,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
fun AskToJoinScreen(
    uiState: JoinUiState,
    joinData: JoinData,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Scaffolds.Backable("Wanna Play", onBack = joinData.onNoPlay) { pad ->
            Column(modifier.padding(pad)) {
                OutlinedTextField(
                    value = uiState.player.nickname.valueOrEmpty(),
                    onValueChange = joinData.onChangeNickname,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { joinData.onChangeNickname }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    enabled = true,
                    readOnly = false,
                    maxLines = 1,
                    shape = RoundedCornerShape(8.dp),

                    label = {
                        Text("Change you nickname?")
                    },
                )
                val m = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
                Button(joinData.onYesPlay, content = {
                    Text("Yes")
                }, modifier = m)

                Button(joinData.onNoPlay, content = {
                    Text("No")
                }, modifier = m)
            }
        }
    }
}

data class JoinData(
    val onChangeNickname: (String) -> Unit,
    val onYesPlay: () -> Unit,
    val onNoPlay: () -> Unit
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AskToJoinPreview() {
    val d = JoinData( {}, {}, {})
    AskToJoinScreen(JoinUiState(Uuid.NIL, player = Player(Uuid.NIL, "oofster")), d)
}
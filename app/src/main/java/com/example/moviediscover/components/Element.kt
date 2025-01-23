package com.example.moviediscover.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.moviediscover.data.ToastState
import com.example.moviediscover.data.getToastMessage
import com.example.moviediscover.network.NetworkStateUtil
import com.example.moviediscover.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoryChips(
    categoryList: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(categoryList) {
            FilterChip(
                label = {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = true,
                shape = CircleShape,
                onClick = {},
            )
        }
    }
}

@Composable
fun Header(header: String, modifier: Modifier = Modifier) {
    Text(
        text = getStyledText(header),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun HeaderWithSeeMoreButton(
    header: String,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Header(header = header, modifier = Modifier.align(Alignment.CenterVertically))
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = onSeeMoreClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "See More >",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun IndeterminateCircularIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun getStyledText(title: String) = buildAnnotatedString {
    withStyle(
        style = MaterialTheme.typography.titleLarge.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.primary,
        )
    ) { append("# ") }
    append(title)
}

@Composable
fun BackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FloatingBackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onBack,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(16.dp)
            .size(48.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun HandleToast(toastState: ToastState, resetToastState: () -> Unit) {
    var currentToast by remember { mutableStateOf<Toast?>(null) }
    val context = LocalContext.current
    val message = getToastMessage(toastState)

    LaunchedEffect(toastState) {
        if (toastState != ToastState.Hidden) {
            currentToast?.cancel()
            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()
            resetToastState()
        }
    }
}

@Composable
fun NetworkStatusSnackbar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel()
) {
    val isConnected by NetworkStateUtil.isConnectedFlow.collectAsState()
    val isDismissedManually by mainViewModel.isSnackbarDismissManually.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Pair(isConnected, isDismissedManually)) {
        try {
            if (isDismissedManually) {
                snackbarHostState.currentSnackbarData?.dismiss()
            } else if (!isConnected) {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "No Internet Connection",
                        actionLabel = "Close",
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        mainViewModel.updateIsSnackbarDismissManually(true)
                    }
                }
            } else {
                snackbarHostState.currentSnackbarData?.dismiss()
                mainViewModel.updateIsSnackbarDismissManually(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    SnackbarHost(hostState = snackbarHostState, modifier = modifier)
}

@Composable
fun ErrorDialog(
    message: String,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = "Error") },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm") }
        },
        onDismissRequest = onConfirm,
    )
}
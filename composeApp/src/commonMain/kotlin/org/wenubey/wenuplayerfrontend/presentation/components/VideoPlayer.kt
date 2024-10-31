package org.wenubey.wenuplayerfrontend.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.wenubey.wenuplayerfrontend.presentation.viewmodel.MainViewModel

@Composable
expect fun VideoPlayer(modifier: Modifier = Modifier, mainViewModel: MainViewModel)
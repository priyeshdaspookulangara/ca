package com.example.chittycollectionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chittycollectionapp.ui.theme.ChittyCollectionAppTheme
import com.example.chittycollectionapp.ui.viewmodel.MainViewModel
import com.example.chittycollectionapp.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChittyCollectionAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val factory = ViewModelFactory(context.applicationContext as Application, (context.applicationContext as BaseApplication).repository)
                    val viewModel: MainViewModel = viewModel(factory = factory)
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val chittyGroups by viewModel.chittyGroups.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.loadInitialData(uri)
        }
    }

    Column {
        Button(onClick = { launcher.launch("application/json") }) {
            Text("Load Initial Data")
        }

        LazyColumn {
            items(chittyGroups) { chittyGroup ->
                Text(text = chittyGroup.chittyName)
            }
        }
    }
}

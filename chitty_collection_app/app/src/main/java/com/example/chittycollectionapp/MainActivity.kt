package com.example.chittycollectionapp

import android.app.Application
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.model.Member
import com.example.chittycollectionapp.ui.theme.ChittyCollectionAppTheme
import com.example.chittycollectionapp.ui.viewmodel.CollectionViewModel
import com.example.chittycollectionapp.ui.viewmodel.DetailsViewModel
import com.example.chittycollectionapp.ui.viewmodel.MainViewModel
import com.example.chittycollectionapp.ui.viewmodel.ViewModelFactory
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChittyCollectionAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val application = context.applicationContext as BaseApplication
                    AppNavigation(application)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(application: BaseApplication) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val factory = ViewModelFactory(application, application.repository)
            val viewModel: MainViewModel = viewModel(factory = factory)
            MainScreen(viewModel = viewModel, navController = navController)
        }
        composable("details/{chittyId}") { backStackEntry ->
            val chittyId = backStackEntry.arguments?.getString("chittyId")
            val factory = ViewModelFactory(application, application.repository, chittyId)
            val viewModel: DetailsViewModel = viewModel(factory = factory)
            DetailsScreen(viewModel = viewModel, navController = navController)
        }
        composable("collection/{chittyId}/{memberId}") { backStackEntry ->
            val chittyId = backStackEntry.arguments?.getString("chittyId")
            val memberId = backStackEntry.arguments?.getString("memberId")
            val factory = ViewModelFactory(application, application.repository, chittyId, memberId)
            val viewModel: CollectionViewModel = viewModel(factory = factory)
            CollectionScreen(viewModel = viewModel, navController = navController)
        }
        composable("settings") {
            val factory = ViewModelFactory(application, application.repository)
            val viewModel: MainViewModel = viewModel(factory = factory)
            SettingsScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavHostController) {
    val chittyGroups by viewModel.chittyGroups.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searchMode) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search Chitty Groups") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Chitty Groups")
                    }
                },
                actions = {
                    IconButton(onClick = { searchMode = !searchMode }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                navController.navigate("settings")
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(chittyGroups) { chittyGroup ->
                ChittyGroupCard(chittyGroup = chittyGroup, onClick = {
                    navController.navigate("details/${chittyGroup.chittyId}")
                })
            }
        }
    }
}

@Composable
fun ChittyGroupCard(chittyGroup: ChittyGroup, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = chittyGroup.chittyName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: ${chittyGroup.chittyAmount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Members: ${chittyGroup.members.size}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(viewModel: DetailsViewModel, navController: NavHostController) {
    val chittyGroup by viewModel.chittyGroup.collectAsState()
    val filteredMembers by viewModel.filteredMembers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var searchMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searchMode) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Search Members") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(chittyGroup?.chittyName ?: "Details")
                    }
                },
                actions = {
                    IconButton(onClick = { searchMode = !searchMode }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(filteredMembers) { member ->
                MemberCard(member = member, onClick = {
                    navController.navigate("collection/${member.chittyId}/${member.memberId}")
                })
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = member.memberName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Contact: ${member.contactNumber}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Due Date: ${member.dueDate}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(viewModel: CollectionViewModel, navController: NavHostController) {
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var paymentStatus by remember { mutableStateOf("Paid") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Collection") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenu(
                label = "Payment Method",
                options = listOf("Cash", "Bank Transfer"),
                selectedOption = paymentMethod,
                onOptionSelected = { paymentMethod = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenu(
                label = "Payment Status",
                options = listOf("Paid", "Pending"),
                selectedOption = paymentStatus,
                onOptionSelected = { paymentStatus = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.saveCollection(
                        amount.toLongOrNull() ?: 0,
                        paymentMethod,
                        paymentStatus,
                        notes
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var apiSyncEnabled by remember { mutableStateOf(false) }
    var apiUrl by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.loadInitialData(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Button(
                onClick = { launcher.launch("application/json") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Initial Data")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    showDatePicker(context) { startDate, endDate ->
                        viewModel.exportCollections(startDate, endDate)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download Collections")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("API Sync Mode", modifier = Modifier.weight(1f))
                Switch(
                    checked = apiSyncEnabled,
                    onCheckedChange = { apiSyncEnabled = it }
                )
            }
            if (apiSyncEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    label = { Text("API Endpoint URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun showDatePicker(context: android.content.Context, onDateRangeSelected: (String, String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, startYear: Int, startMonth: Int, startDayOfMonth: Int ->
            val endDatePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, endYear: Int, endMonth: Int, endDayOfMonth: Int ->
                    val startDate = "$startYear-${startMonth + 1}-$startDayOfMonth"
                    val endDate = "$endYear-${endMonth + 1}-$endDayOfMonth"
                    onDateRangeSelected(startDate, endDate)
                },
                year,
                month,
                day
            )
            endDatePickerDialog.show()
        },
        year,
        month,
        day
    )
    startDatePickerDialog.show()
}

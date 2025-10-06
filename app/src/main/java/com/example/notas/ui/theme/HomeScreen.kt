package com.example.notas.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

// --------------------------- DATA (mock) ---------------------------

data class NoteItem(
    val id: Long,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime
)

enum class TaskVisualState { PENDING, DONE, OVERDUE }

data class TaskItem(
    val id: Long,
    val title: String,
    val description: String,
    val dueAt: LocalDateTime,
    val state: TaskVisualState
)

// --------------------------- ROOT SCREEN ---------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onSortClick: (tabIndex: Int) -> Unit = {},
    initialTab: Int = 0,
) {
    var selectedTab by remember { mutableStateOf(initialTab) }      // 0 Notas, 1 Tareas
    var query by remember { mutableStateOf("") }

    // Demo data (reemplaza con Room más adelante)
    val demoNotes = remember {
        listOf(
            NoteItem(1, "Nota 1", "Descripción nota 1",
                LocalDateTime.now().minusDays(1).withHour(5).withMinute(2)),
            NoteItem(2, "Nota 2", "Descripción nota 2",
                LocalDateTime.now().minusDays(0).withHour(6).withMinute(12)),
            NoteItem(3, "Nota 3", "Descripción nota 3",
                LocalDateTime.now().plusDays(1).withHour(23).withMinute(59)),
            NoteItem(4, "Nota 4", "Descripción nota 4",
                LocalDateTime.now().withHour(11).withMinute(59))
        )
    }
    val demoTasks = remember {
        listOf(
            TaskItem(1, "Tarea 1", "Descripción tarea 1",
                LocalDateTime.now().minusDays(1), TaskVisualState.DONE),
            TaskItem(2, "Tarea 2", "Descripción tarea 2",
                LocalDateTime.now().minusHours(3), TaskVisualState.OVERDUE),
            TaskItem(3, "Tarea 3", "Descripción tarea 3",
                LocalDateTime.now().plusDays(1).withHour(23).withMinute(59), TaskVisualState.PENDING),
            TaskItem(4, "Tarea 4", "Descripción tarea 4",
                LocalDateTime.now().plusDays(3).withHour(23).withMinute(59), TaskVisualState.PENDING)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF99D6FF),        // azul suave como en tu mockup
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Agregar")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            // Header + sort
            HeaderWithSort(
                title = if (selectedTab == 0) "Notas" else "Tareas",
                onSortClick = { onSortClick(selectedTab) }
            )

            // Search
            SearchBar(
                value = query,
                onValueChange = { query = it },
                placeholder = if (selectedTab == 0)
                    "Search" else "Search"
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(text = "Notas") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(text = "Tareas") }
                )
            }

            Spacer(Modifier.height(8.dp))

            if (selectedTab == 0) {
                NotesList(
                    notes = demoNotes.filter { filterNote(it, query) }
                )
            } else {
                TasksList(
                    tasks = demoTasks.filter { filterTask(it, query) }
                )
            }
        }
    }
}

// --------------------------- COMPONENTS ---------------------------

@Composable
private fun HeaderWithSort(
    title: String,
    onSortClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSortClick) {
            Icon(Icons.Rounded.SwapVert, contentDescription = "Ordenar")
        }
    }
    Divider()
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        trailingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null)
        }
    )
    Spacer(Modifier.height(10.dp))
}

// --------------------------- NOTES ---------------------------

@Composable
private fun NotesList(notes: List<NoteItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(note = note)
        }
    }
}

@Composable
private fun NoteCard(note: NoteItem) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                // Fecha en dos líneas (dd MMM / HH:mm) y en itálica
                val (line1, line2) = dateTwoLines(note.createdAt)
                Text(line1, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                Text(line2, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
            }
        }
    }
}

// --------------------------- TASKS ---------------------------

@Composable
private fun TasksList(tasks: List<TaskItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(task = task)
        }
    }
}

@Composable
private fun TaskCard(task: TaskItem) {
    val statusColor = when (task.state) {
        TaskVisualState.DONE -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        TaskVisualState.OVERDUE -> Color(0xFFD32F2F) // rojo para "Vencida"
        TaskVisualState.PENDING -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                when (task.state) {
                    TaskVisualState.DONE -> {
                        Text(
                            "Cumplida",
                            color = statusColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.End
                        )
                    }
                    TaskVisualState.OVERDUE -> {
                        Text(
                            "Vencida",
                            color = statusColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.End
                        )
                    }
                    TaskVisualState.PENDING -> {
                        val (line1, line2) = dueTwoLines(task.dueAt)
                        Text(line1, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                        Text(line2, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                    }
                }
            }
        }
    }
}

// --------------------------- HELPERS ---------------------------

private fun dateTwoLines(dt: LocalDateTime): Pair<String, String> {
    // ej: "30 Sep" / "05:02"
    val line1 = dt.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
    val line2 = dt.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
    return line1 to line2
}

private fun dueTwoLines(dt: LocalDateTime): Pair<String, String> {
    // Para tareas: "Hoy" / "11:59", "Mañana" / "23:59", o "Lunes" / "23:59"
    val now = LocalDateTime.now()
    val sameDay = dt.toLocalDate() == now.toLocalDate()
    val tomorrow = dt.toLocalDate() == now.toLocalDate().plusDays(1)

    val dayLabel = when {
        sameDay -> "Hoy"
        tomorrow -> "Mañana"
        else -> dt.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    val hour = dt.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    return dayLabel to hour
}

private fun filterNote(item: NoteItem, query: String): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    return item.title.contains(q, ignoreCase = true) ||
            item.description.contains(q, ignoreCase = true)
}

private fun filterTask(item: TaskItem, query: String): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    return item.title.contains(q, ignoreCase = true) ||
            item.description.contains(q, ignoreCase = true)
}

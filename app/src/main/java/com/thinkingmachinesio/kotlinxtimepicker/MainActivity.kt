package com.thinkingmachinesio.kotlinxtimepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thinkingmachinesio.kotlinxtimepicker.ui.components.TimePickerDialog
import com.thinkingmachinesio.kotlinxtimepicker.ui.theme.KotlinxTimePickerTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinxTimePickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        MeetingTimeSelectionCard(
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingTimeSelectionCard(
    modifier: Modifier = Modifier
) {
    //get current time for time picker defaults
    val now = Clock.System.now()
    val currentTimeZone = TimeZone.currentSystemDefault()

    val staticNowDateTime = now.toLocalDateTime(currentTimeZone)

    //get start of day for date picker defaults
    val startOfDay = staticNowDateTime.date.atStartOfDayIn(currentTimeZone)
    val todayDateMilliseconds = startOfDay.toJavaInstant().toEpochMilli()

    //Custom saver for LocalDateTime
    val localDateTimeSaver = run {
        mapSaver(
            save = { mapOf("timestamp" to it.toInstant(currentTimeZone).toEpochMilliseconds()) },
            restore = {
                Instant.fromEpochMilliseconds(it["timestamp"] as Long).toLocalDateTime(currentTimeZone)
            }
        )
    }


    var currentSelectedDate by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(staticNowDateTime)
    }

    val timeInFuture by remember {
        derivedStateOf {
            currentSelectedDate.time > staticNowDateTime.time
        }
    }

    val dayInFuture by remember {
        derivedStateOf {
            currentSelectedDate.date > staticNowDateTime.date
        }
    }


    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayDateMilliseconds,
        initialDisplayedMonthMillis = todayDateMilliseconds
    )

    var showDatePickerState by remember {
        mutableStateOf(false)
    }


    var timePickerState = rememberTimePickerState(currentSelectedDate.hour,
        currentSelectedDate.minute,
        is24Hour = false)
    var showTimePickerState by remember {
        mutableStateOf(false)
    }

    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(text = "Meeting Time Selection")



            FilledTonalButton(
                onClick = { showTimePickerState = true }) {
                Icon(
                    Icons.Outlined.AccessTime,
                    contentDescription = "Select Time"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (!timeInFuture && !dayInFuture) "Now" else formatTimeJava(
                        currentSelectedDate.toJavaLocalDateTime()
                    )
                )
                Icon(
                    Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Select Time"
                )
            }

            FilledTonalButton(onClick = { showDatePickerState = true }) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = "Select Date"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (!dayInFuture) "Today" else formatDateJava(currentSelectedDate.toJavaLocalDateTime()))
                Icon(
                    Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Select Date"
                )
            }

            Text(text = "Meeting Scheduled: ${formatDateAndTimeJava(currentSelectedDate.toJavaLocalDateTime())}")

            if (showTimePickerState) {
                TimePickerDialog(
                    onCancel = { showTimePickerState = false },
                    onConfirm = {
                        val newTime = LocalTime(timePickerState.hour, timePickerState.minute)
                        currentSelectedDate = currentSelectedDate.date.atTime(newTime)
                        showTimePickerState = false
                    },
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            if (showDatePickerState) {
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePickerState = false
                    },
                    confirmButton = {
                        currentSelectedDate =
                            datePickerState.selectedDateMillis?.let { selectedMillis ->


                                val selectedDate =
                                    Instant.fromEpochMilliseconds(selectedMillis).toLocalDateTime(
                                        TimeZone.UTC
                                    ).date
                                val preservedTime = currentSelectedDate.time

                                LocalDateTime(selectedDate, preservedTime)

                            } ?: currentSelectedDate // Fallback to the current selection if null

                    },
                ) {
                    Column {
                        DatePicker(state = datePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(modifier = Modifier.padding(end = 8.dp), onClick = {
                                showDatePickerState = false

                            }) {
                                Text("Cancel")
                            }
                            Button(modifier = Modifier.padding(end = 16.dp), onClick = {

                                showDatePickerState = false
                            }) {
                                Text("OK")
                            }

                        }
                    }

                }
            }
        }
    }
}

fun formatTimeJava(time: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return time.format(formatter)
}

fun formatDateJava(date: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yy")
    return date.format(formatter)
}

fun formatDateAndTimeJava( date: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm a")
    return date.format(formatter)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinxTimePickerTheme {
        Surface {
            Column {
                Text(text = "Hello, World!")
            }
        }
    }
}


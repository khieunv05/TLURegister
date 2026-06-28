package com.example.registersubjecttlu.presentation.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registersubjecttlu.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(viewModel : MainScreenViewModel,callback: () -> Unit,
                        profileCallback:()-> Unit){
    var selectedTab by remember { mutableStateOf(ScreenTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Xin chào ${viewModel.displayName ?: "" }") },
            actions = {
                IconButton(onClick = { viewModel.loadMainScreenData() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            })},
        bottomBar = {
            NavigationBar() {
                ScreenTab.entries.forEach {tab->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {selectedTab = tab},
                        icon = {Icon(tab.icon, contentDescription = selectedTab.title)}

                    )
                }
            }
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ){
            when(selectedTab){
                ScreenTab.HOME -> MainScreen(viewModel, snackbarHostState, callback)
                else -> ProfileScreen({ viewModel.logout()
                profileCallback()
                })
            }
        }
    }

}
@Composable
fun ProfileScreen(callback: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        OutlinedButton(onClick = {
            callback()
        }) {
            Text("Đăng xuất", style = MaterialTheme.typography.titleMedium)
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainScreenViewModel, snackbarHostState: SnackbarHostState, callback:()-> Unit){
    val uiState by viewModel.uiState.collectAsState()
    var lastSuccessData by remember { mutableStateOf<CourseResponse?>(null) }
    var semesterExpanded by remember { mutableStateOf<Boolean>( false) }
    var secondExpanded by remember { mutableStateOf(false) }
    val semesterOptions by viewModel.semesterOptions.collectAsState()
    val selectedSecondOption by viewModel.selectedSecondOption.collectAsState()
    val selectedSemesterOption by viewModel.selectedSemesterOption.collectAsState()
    val secondOptions by viewModel.secondOptions.collectAsState()
    val isLoading = semesterOptions.isEmpty()
    LaunchedEffect(uiState) {
        when(uiState) {
            is MainScreenUIState.Success -> {
                lastSuccessData = (uiState as MainScreenUIState.Success).courseResponse
            }
            is MainScreenUIState.RegisterSuccess -> {
                snackbarHostState.showSnackbar((uiState as MainScreenUIState.RegisterSuccess).registerCourseResponse.message)
                viewModel.loadingCourse()
            }
            is MainScreenUIState.RegisterError -> {
                snackbarHostState.showSnackbar((uiState as MainScreenUIState.RegisterError).message)
            }
            is MainScreenUIState.RemoveRegisterSuccess->{
                snackbarHostState.showSnackbar((uiState as MainScreenUIState.RemoveRegisterSuccess).message)
                viewModel.loadingCourse()
            }
            is MainScreenUIState.RemoveRegisterError->{
                snackbarHostState.showSnackbar((uiState as MainScreenUIState.RemoveRegisterError).message)
            }
            is MainScreenUIState.NavigateToLoginScreen -> {
                callback()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()){

        Column(modifier = Modifier.padding(16.dp)) {


            Row() {
                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(1f),
                    expanded = semesterExpanded,
                    onExpandedChange = { semesterExpanded = !semesterExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSemesterOption?.semesterName ?: "Chọn học kỳ",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).weight(1f),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = semesterExpanded,
                        onDismissRequest = { semesterExpanded = false }
                    ) {
                        semesterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option.semesterName ?: "") },
                                onClick = {
                                    viewModel.onSemesterOptionChange(option)
                                    semesterExpanded = false
                                }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.width(16.dp))

                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(1f),
                    expanded = secondExpanded,
                    onExpandedChange = { secondExpanded = !secondExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSecondOption?.name ?: "Chọn học kỳ",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = secondExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = secondExpanded,
                        onDismissRequest = { secondExpanded = false }
                    ) {
                        secondOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option.name ?: "") },
                                onClick = {
                                    viewModel.onSelectedSecondOptionChange(option)
                                    viewModel.loadingCourse()
                                    secondExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            lastSuccessData?.let { response ->
                val registrations = response.courseRegisterViewObject?.subjectRegistrations ?: emptyList()
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(registrations) { subject ->
                        var isExpanded by rememberSaveable { mutableStateOf(false) }
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { isExpanded = !isExpanded }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = subject.subjectName ?: "Không tên",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if(isExpanded){
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        subject.courseSubjects?.forEach { course ->
                                            if (course.subCourseSubjects.isNullOrEmpty()) {
                                                CourseItem(course = course, onRegisterClick = {
                                                    viewModel.registerSubject(
                                                        RegisterPeriodRequest(course.id, course.subjectId)
                                                    )
                                                }, onRemoveRegisterClick = {
                                                    viewModel.removeRegister(
                                                        RegisterPeriodRequest(course.id,course.subjectId)
                                                    )
                                                })
                                            } else {
                                                TheoryWithSubCoursesItem(theoryCourse = course, onSubRegisterClick = { sub ->
                                                    viewModel.registerSubject(
                                                        RegisterPeriodRequest(sub.id, sub.subjectId)
                                                    )
                                                }, onSubRemoveRegisterClick = {sub->
                                                    viewModel.removeRegister(
                                                        RegisterPeriodRequest(sub.id,sub.subjectId)
                                                    )
                                                })
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        when(uiState) {
            is MainScreenUIState.Loading -> {
                if (lastSuccessData == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }
            }
            is MainScreenUIState.Error -> {
                if (lastSuccessData == null) {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = (uiState as MainScreenUIState.Error).message)
                    }
                }
            }
            is MainScreenUIState.LoadSemesterSuccess->{
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = (uiState as MainScreenUIState.LoadSemesterSuccess).message)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun CourseItem(course: CourseSubject, onRegisterClick: () -> Unit,onRemoveRegisterClick:()-> Unit) {
    val overLap = course.overLapClasses ?: emptyList()
    val isConflict = overLap.isNotEmpty()
    val isRegistered = course.isSelected
    val isFull = course.numberStudent >= course.maxStudent

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = course.displayName ?: "Học phần",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (isRegistered || isConflict) {
                    Surface(
                        color = if (isRegistered) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (isRegistered) "Đã đăng ký" else "Bị trùng lịch",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isRegistered) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sĩ số: ${course.numberStudent}/${course.maxStudent}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isFull) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = "Lịch học:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            val tables = course.timetables ?: emptyList()
            tables.forEach { timeTable ->
                TimeTableEntry(timeTable)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = if(!isRegistered) onRegisterClick else onRemoveRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isRegistered ||(!isConflict && !isFull),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Text(
                    text = when {
                        isRegistered -> "Hủy đăng kí học"
                        isConflict -> "Lịch học bị trùng"
                        isFull -> "Lớp đã đầy"
                        else -> "Đăng ký ngay"
                    }
                )
            }
        }
    }
}

@Composable
fun TheoryWithSubCoursesItem(
    theoryCourse: CourseSubject,
    onSubRegisterClick: (SubCourseSubject) -> Unit,
    onSubRemoveRegisterClick:(SubCourseSubject)-> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lớp Lý Thuyết: ${theoryCourse.displayName ?: ""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            val tables = theoryCourse.timetables ?: emptyList()
            tables.forEach { TimeTableEntry(it) }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "Chọn lớp thực hành:",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            theoryCourse.subCourseSubjects?.forEach { sub ->
                SubCourseItem(sub, onRegisterClick = { onSubRegisterClick(sub) }, onRemoveRegisterClick = {onSubRemoveRegisterClick(sub)})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SubCourseItem(sub: SubCourseSubject, onRegisterClick: () -> Unit,onRemoveRegisterClick: () -> Unit) {
    val overLap = sub.overLapClasses ?: emptyList()
    val isConflict = overLap.isNotEmpty()
    val isFull = sub.numberStudent >= sub.maxStudent

    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        border = if (sub.isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sub.displayName ?: "Thực hành",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (sub.isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            val tables = sub.timetables ?: emptyList()
            tables.forEach { TimeTableEntry(it) }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sĩ số: ${sub.numberStudent}/${sub.maxStudent}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFull) MaterialTheme.colorScheme.error else Color.Unspecified
                )

                Button(
                    onClick = if(!sub.isSelected) onRegisterClick else onRemoveRegisterClick,
                    modifier = Modifier.height(36.dp),
                    enabled = sub.isSelected || (!isConflict && !isFull),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when {
                            sub.isSelected -> "Hủy đăng kí"
                            isConflict -> "Trùng"
                            isFull -> "Đầy"
                            else -> "Chọn lớp"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun TimeTableEntry(timeTable: TimeTable) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${fromIntWeekIndexToWeekIndex(timeTable.weekIndex)} (Tuần ${timeTable.fromWeek}-${timeTable.toWeek})",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Icon(Icons.Default.Schedule, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${timeTable.start} - ${timeTable.end}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.width(16.dp))
            Icon(Icons.Default.Room, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = timeTable.roomName ?: "Không rõ phòng",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Icon(Icons.Default.Person, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "GV: ${timeTable.teacherName ?: "Chưa rõ"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun fromIntWeekIndexToWeekIndex(weekIndex: Int) : String{
    return when(weekIndex){
        2 -> "Thứ hai"
        3 -> "Thứ ba"
        4 -> "Thứ tư"
        5 -> "Thứ năm"
        6 -> "Thứ sáu"
        7 -> "Thứ bảy"
        8 -> "Chủ nhật"
        else -> "Lỗi"
    }
}

package com.example.registersubjecttlu.presentation.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registersubjecttlu.data.local.TokenStorage
import com.example.registersubjecttlu.data.local.entity.SemesterEntity
import com.example.registersubjecttlu.domain.repository.CourseRepository
import com.example.registersubjecttlu.domain.repository.SemesterRepository
import com.example.registersubjecttlu.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val courseRepository: CourseRepository,
    private val tokenStorage: TokenStorage
): ViewModel() {
    private val _uiState = MutableStateFlow<MainScreenUIState>(MainScreenUIState.Loading)
    val uiState: StateFlow<MainScreenUIState> = _uiState
    init {
        loadMainScreenData()
    }
    fun loadMainScreenData(){
        viewModelScope.launch {
                _uiState.value = MainScreenUIState.Loading
                var studentId = tokenStorage.studentIdFlow.first()
                if(studentId==null){
                    try{
                        val studentResponse = studentRepository.getStudent()
                        tokenStorage.saveStudentId(studentResponse.id)
                        studentId = tokenStorage.studentIdFlow.first()
                    }
                    catch (e: Exception){
                        _uiState.value = MainScreenUIState.Error("Lỗi khi lấy dữ liệu sinh viên")
                        return@launch
                    }

                }
                println("StudentId: $studentId")
                try{
                    semesterRepository.syncSemestersWithServer()
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
                try {
                    semesterRepository.getSemesterFromLocal().collectLatest {
                            localSemesters-> val semesterId = findActiveSemesterId(localSemesters) ?: 0
                            println("SemesterId: $semesterId")
                        if(semesterId != 0){
                            try{
                                val courseResponse = courseRepository.getCourse(studentId?:0,semesterId)
                                _uiState.value = MainScreenUIState.Success(courseResponse)
                            }
                            catch (e: Exception){
                                _uiState.value = MainScreenUIState.Error("Không thể tải danh sách môn học")
                            }
                        }
                        else{
                            _uiState.value = MainScreenUIState.Error("Hiện tại trường không mở đợt đăng kí nào")
                        }
                    }
                }
                catch (e: Exception){
                    _uiState.value = MainScreenUIState.Error("Lỗi truy xuất cơ sở dữ liệu nội bộ")
                }
        }

    }
    private fun findActiveSemesterId(semesters:List<SemesterEntity>): Int?{
        val currentTime = System.currentTimeMillis()
        for (semester in semesters){
            semester.semesterRegisterPeriods?.forEach {
                periods->
                val startTime = periods.startRegisterTime
                val endTime = periods.endRegisterTime
                val isLock = periods.isLockRegister
                val isValid = currentTime in startTime..endTime && !isLock
                if(isValid){
                    return periods.id
                }
            }
        }
        return semesters.find { it.isCurrent }?.semesterRegisterPeriods?.firstOrNull()?.id
    }
}
package com.example.registersubjecttlu.presentation.main_screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registersubjecttlu.data.local.TokenStorage
import com.example.registersubjecttlu.data.local.entity.SemesterEntity
import com.example.registersubjecttlu.data.local.entity.SemesterRegisterPeriodEntity
import com.example.registersubjecttlu.domain.model.RegisterPeriodRequest
import com.example.registersubjecttlu.domain.repository.CourseRepository
import com.example.registersubjecttlu.domain.repository.SemesterRepository
import com.example.registersubjecttlu.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
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
    var displayName by mutableStateOf<String?>(null)
        private set
    private val _semesterOptions = MutableStateFlow<List<SemesterEntity>>(emptyList())
    val semesterOptions : StateFlow<List<SemesterEntity>> = _semesterOptions
    private val _secondOptions = MutableStateFlow<List<SemesterRegisterPeriodEntity>>(emptyList())
    val secondOptions : StateFlow<List<SemesterRegisterPeriodEntity>> = _secondOptions
    private val _selectedSecondOption = MutableStateFlow<SemesterRegisterPeriodEntity?>(null)
    val selectedSecondOption : StateFlow<SemesterRegisterPeriodEntity?> = _selectedSecondOption
    private val _selectedSemesterOption = MutableStateFlow<SemesterEntity?>(null)
    val selectedSemesterOption : StateFlow<SemesterEntity?> = _selectedSemesterOption
    init {
        loadMainScreenData()
    }
    fun loadMainScreenData(){
        var isLoadingStudent = true
        var isLoadingSemester = true
        var isLoadingCourse = true
        viewModelScope.launch {
                _uiState.value = MainScreenUIState.Loading
                var studentId = tokenStorage.studentIdFlow.first()
                var lastFetchDataTime = tokenStorage.timeFlow.first() ?: 0
                val systimeTime = System.currentTimeMillis()
                val oneDayMillis = TimeUnit.DAYS.toMillis(1)
                if(studentId==null){
                    while (isLoadingStudent){
                        try{
                            val studentResponse = studentRepository.getStudent()
                            tokenStorage.saveStudentId(studentResponse.id)
                            displayName = studentResponse.displayName
                            studentId = tokenStorage.studentIdFlow.first()
                            isLoadingStudent = false
                        }
                        catch (e: Exception){
                            if(e is HttpException && e.code() == 401){
                                _uiState.value = MainScreenUIState.NavigateToLoginScreen
                                tokenStorage.clearToken()
                                return@launch
                            }
                            _uiState.value = MainScreenUIState.Error("Lỗi khi lấy dữ liệu sinh viên,đang tự động tải lại")
                            delay(5000)
                        }
                    }

                }
                println("StudentId: $studentId")
                if(systimeTime - lastFetchDataTime > oneDayMillis){
                    while (isLoadingSemester){
                        try{
                            semesterRepository.syncSemestersWithServer()
                            tokenStorage.saveTime(systimeTime)
                            isLoadingSemester = false
                        }
                        catch (e: Exception){
                            e.printStackTrace()
                            if(e is HttpException && e.code() == 401){
                                _uiState.value = MainScreenUIState.NavigateToLoginScreen
                                tokenStorage.clearToken()
                                return@launch
                            }
                            delay(5000)
                        }
                    }
                }

                try {

                    semesterRepository.getSemesterFromLocal().collectLatest {
                            localSemesters->
                        _semesterOptions.value = localSemesters
                        _uiState.value = MainScreenUIState.LoadSemesterSuccess("Vui lòng chọn học kì")
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
    fun registerSubject(registerPeriodRequest: RegisterPeriodRequest) {
        var isRegisterSubject = true
        viewModelScope.launch {
            while (isRegisterSubject){
                try{
                    _uiState.value = MainScreenUIState.Loading
                    val studentId = tokenStorage.studentIdFlow.first() ?: 0
                    val semesterId = tokenStorage.semesterIdFlow.first() ?:0
                    val registerCourseResponse = courseRepository.registerCourse(studentId,semesterId,registerPeriodRequest)
                    _uiState.value = MainScreenUIState.RegisterSuccess(registerCourseResponse)
                    isRegisterSubject = false
                }
                catch (e: Exception){
                    if(e is HttpException && e.code() == 401){
                        _uiState.value = MainScreenUIState.NavigateToLoginScreen
                        tokenStorage.clearToken()
                        return@launch
                    }
                    _uiState.value = MainScreenUIState.RegisterError("Đăng ký thất bại,đang tự động thử lại")
                    delay(5000)
                }
            }
        }
    }
    fun logout(){
        viewModelScope.launch {
            tokenStorage.clearToken()
        }
    }
    fun removeRegister(registerPeriodRequest: RegisterPeriodRequest){
        var isRemoveRegister = true
        viewModelScope.launch {
           while (isRemoveRegister){
               try {
                   _uiState.value = MainScreenUIState.Loading
                   val studentId = tokenStorage.studentIdFlow.first()
                   val semesterId = tokenStorage.semesterIdFlow.first()
                   val courseResponse = courseRepository.removeRegister(studentId?:0,semesterId?:0,registerPeriodRequest)
                   isRemoveRegister = false
                   _uiState.value = MainScreenUIState.RemoveRegisterSuccess(courseResponse.message)
               }
               catch (e: Exception){
                   if(e is HttpException && e.code() == 401){
                       _uiState.value = MainScreenUIState.NavigateToLoginScreen
                       tokenStorage.clearToken()
                       return@launch
                   }
                   println("Lỗi ${e.message}")
                   _uiState.value = MainScreenUIState.RemoveRegisterError("Hủy đăng ký thất bại, tự thử lại sau 5s")
                    delay(5000)

               }
           }
        }
    }
    fun loadingCourse(){
        var isLoading = true
        viewModelScope.launch {
            while (isLoading){
                try{
                    _uiState.value = MainScreenUIState.Loading
                    val studentId = tokenStorage.studentIdFlow.first() ?: 0

                    val courseResponse = courseRepository.getCourse(studentId,selectedSecondOption.value?.id ?: 0)
                    isLoading = false
                    _uiState.value = MainScreenUIState.Success(courseResponse)
                }
                catch (e: Exception){
                    if(e is HttpException && e.code() == 401){
                        _uiState.value = MainScreenUIState.NavigateToLoginScreen
                        tokenStorage.clearToken()
                        return@launch
                    }
                    println("Lỗi ${e.message}")
                    _uiState.value = MainScreenUIState.Error("Lấy danh sách môn học thất bại, tự thử lại sau 5s")
                    delay(5000)
                }
            }

        }
    }
    fun onSelectedSecondOptionChange(newSelectedOption: SemesterRegisterPeriodEntity){
        _selectedSecondOption.value = newSelectedOption
    }
    fun onSemesterOptionChange(newSemesterOption : SemesterEntity){
        _selectedSemesterOption.value = newSemesterOption
        _secondOptions.value = newSemesterOption.semesterRegisterPeriods ?: emptyList()
    }
}
package com.example.registersubjecttlu.domain.model

import com.google.gson.annotations.SerializedName

data class CourseResponse(val courseRegisterViewObject: CourseRegisterViewObject)
data class CourseRegisterViewObject(@SerializedName("listSubjectRegistrationDtos") val subjectRegistrations: List<SubjectRegistration>)
data class SubjectRegistration(@SerializedName("courseSubjectDtos") val courseSubjects: List<CourseSubject>,val subjectName: String)
data class CourseSubject(val id: Int,
                         val subjectId: Int,
                         val timetables: List<TimeTable>,
                         val maxStudent: Int,
                         val numberStudent: Int,
                         val displayName: String,
                         val isSelected: Boolean,
                         val overLapClasses: List<String>
                         , @SerializedName("subCourseSubjects") val subCourseSubjects: List<SubCourseSubject>?)
data class SubCourseSubject(val id: Int, val subjectId: Int, val timetables: List<TimeTable>,
                            val maxStudent: Int, val numberStudent: Int, val isSelected: Boolean,
                            val displayName: String, val overLapClasses: List<String>)
data class TimeTable(val weekIndex: Int,val fromWeek: Int,val toWeek: Int,val start: String,val end: String,
                     val teacherName: String,val roomName: String
    )
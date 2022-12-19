package com.niyaj.popos.realm.employee_attendance.domain.util

import com.niyaj.popos.realm.employee_attendance.domain.model.EmployeeAttendance

data class AbsentReport(
    val startDate: String = "",
    val endDate: String = "",
    val absent: List<EmployeeAttendance> = emptyList(),
)

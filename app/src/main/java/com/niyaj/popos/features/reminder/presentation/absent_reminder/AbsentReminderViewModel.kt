package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.EmployeeReminderWithStatusState
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.util.getStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AbsentReminderViewModel @Inject constructor(
    private val reminderUseCases: ReminderUseCases,
    private val attendanceUseCases : AttendanceUseCases,
): ViewModel() {

    private val _employees = MutableStateFlow(EmployeeReminderWithStatusState())
    val employees = _employees.asStateFlow()

    private val _selectedEmployees = mutableStateListOf<String>()
    val selectedEmployees: SnapshotStateList<String> = _selectedEmployees

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    private var count: Int = 0

    init {
        getAbsentReminderEmployee()
    }

    fun onEvent(event : AbsentReminderEvent) {
        when(event) {
            is AbsentReminderEvent.SelectEmployee -> {
                if (_selectedEmployees.contains(event.employeeId)) {
                    _selectedEmployees.remove(event.employeeId)
                }else {
                    _selectedEmployees.add(event.employeeId)
                }
            }

            is AbsentReminderEvent.SelectAllEmployee -> {
                count += 1

                val employees = _employees.value.employees.filter { it.paymentStatus == PaymentStatus.NotPaid }

                if (employees.isNotEmpty()){
                    employees.forEach { employeeList ->
                        if (count % 2 != 0){
                            val selectedEmployee = _selectedEmployees.find { it == employeeList.employee.employeeId }

                            if (selectedEmployee == null){
                                _selectedEmployees.add(employeeList.employee.employeeId)
                            }
                        }else {
                            _selectedEmployees.remove(employeeList.employee.employeeId)
                        }
                    }
                }
            }

            is AbsentReminderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.emit(event.date)

                    getAbsentReminderEmployee(event.date)
                }
            }

            is AbsentReminderEvent.MarkAsAbsent -> {
                viewModelScope.launch {
                    _selectedEmployees.forEach { employeeId ->
                        val employeeWithStatus = _employees.value.employees.find { it.employee.employeeId == employeeId }

                        if (employeeWithStatus != null) {
                            val result = attendanceUseCases.addAbsentEntry(
                                EmployeeAttendance(
                                    employee = employeeWithStatus.employee,
                                    isAbsent = true,
                                    absentDate = _selectedDate.value.ifEmpty { getStartTime },
                                )
                            )

                            when(result) {
                                is Resource.Loading -> {
                                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                                }
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("${employeeWithStatus.employee.employeeName} Marked as absent."))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to mark ${employeeWithStatus.employee.employeeName} as absent."))
                                }
                            }
                        } else {
                            _eventFlow.emit(UiEvent.OnError("Unable to find employee"))
                        }
                    }

                    val markAsCompleted = DateUtils.isToday(_selectedDate.value.toLong())

                    val result = reminderUseCases.createOrUpdateAbsentReminder(AbsentReminder(isCompleted = markAsCompleted))

                    if (result) {
                        _eventFlow.emit(UiEvent.OnSuccess("Selected employee marked as absent on selected date."))
                    }
                }
            }
        }
    }

    private fun getAbsentReminderEmployee(salaryDate: String = _selectedDate.value) {
        viewModelScope.launch {
            reminderUseCases.getReminderEmployees(salaryDate, ReminderType.Attendance).collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        _employees.value = _employees.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _employees.value = _employees.value.copy(
                                employees = it,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _employees.value = _employees.value.copy(error = result.message)
                    }
                }
            }
        }
    }
}
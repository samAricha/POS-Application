package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AttendanceRepository
import com.niyaj.data.repository.validation.AttendanceValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Attendance
import com.niyaj.model.Employee
import com.niyaj.model.filterEmployeeAttendance
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class AttendanceRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : AttendanceRepository, AttendanceValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllEmployee(): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            realm.query<EmployeeEntity>().find().asFlow().mapLatest { employees ->
                employees.collectWithSearch(
                    transform = { it.toExternalModel() },
                    searchFilter = { it }
                )
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: String): Employee? {
        return try {
            withContext(ioDispatcher) {
                realm
                    .query<EmployeeEntity>("employeeId == $0", employeeId)
                    .first()
                    .find()
                    ?.toExternalModel()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllAttendance(searchText: String): Flow<List<Attendance>> {
        return withContext(ioDispatcher) {
            realm
                .query<AttendanceEntity>()
                .sort("absentDate", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { attendance ->
                    attendance.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterEmployeeAttendance(searchText) },
                    )
                }
        }
    }

    override suspend fun getAttendanceById(attendanceId: String): Resource<Attendance?> {
        return try {
            val attendance = withContext(ioDispatcher) {
                realm.query<AttendanceEntity>("attendeeId == $0", attendanceId).first().find()
            }

            Resource.Success(attendance?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Attendance")
        }
    }

    override suspend fun findAttendanceByAbsentDate(
        absentDate: String,
        employeeId: String,
        attendanceId: String?,
    ): Boolean {
        return withContext(ioDispatcher) {
            if (attendanceId == null) {
                realm.query<AttendanceEntity>(
                    "absentDate == $0 AND employee.employeeId == $1",
                    absentDate,
                    employeeId
                ).first().find()
            } else {
                realm.query<AttendanceEntity>(
                    "attendeeId != $0 && absentDate == $1 AND employee.employeeId == $2",
                    attendanceId,
                    absentDate,
                    employeeId
                ).first().find()
            } != null
        }
    }

    override suspend fun addOrUpdateAbsentEntry(
        attendance: Attendance,
        attendanceId: String
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val validateAbsentEmployee =
                    validateAbsentEmployee(attendance.employee?.employeeId ?: "")
                val validateIsAbsent = validateIsAbsent(attendance.isAbsent)
                val validateAbsentDate = validateAbsentDate(
                    attendance.absentDate,
                    attendance.employee?.employeeId ?: "",
                    attendanceId
                )

                val hasError = listOf(
                    validateAbsentEmployee,
                    validateIsAbsent,
                    validateAbsentDate
                ).any { !it.successful }

                if (!hasError) {
                    val employee =
                        realm.query<EmployeeEntity>(
                            "employeeId == $0",
                            attendance.employee?.employeeId
                        ).first().find()

                    if (employee != null) {
                        val newAttendance =
                            realm.query<AttendanceEntity>("attendeeId == $0", attendanceId)
                                .first().find()

                        if (newAttendance != null) {
                            realm.write {
                                findLatest(newAttendance)?.apply {
                                    findLatest(employee).also {
                                        this.employee = it
                                    }

                                    this.isAbsent = attendance.isAbsent
                                    this.absentDate = attendance.absentDate
                                    this.absentReason = attendance.absentReason
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }
                            }

                            Resource.Success(true)
                        } else {
                            realm.write {
                                this.copyToRealm(attendance.toEntity(findLatest(employee)))
                            }

                            Resource.Success(true)
                        }
                    } else {
                        Resource.Error("Employee not found")
                    }
                } else {
                    Resource.Error("Unable to validate attendance")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to add absent entry.")
            }
        }
    }

    override suspend fun removeAttendances(attendanceIds: List<String>): Resource<Boolean> {
        return try {
            attendanceIds.forEach { attendanceId ->
                withContext(ioDispatcher) {
                    val attendance =
                        realm.query<AttendanceEntity>("attendeeId == $0", attendanceId).first()
                            .find()
                    if (attendance != null) {
                        realm.write {
                            findLatest(attendance)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance")
        }
    }

    override suspend fun removeAttendanceByEmployeeId(
        employeeId: String,
        date: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val attendance = realm.query<AttendanceEntity>(
                    "employee.employeeId == $0 AND absentDate == $1",
                    employeeId,
                    date
                ).first().find()

                if (attendance != null) {
                    realm.write {
                        findLatest(attendance)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find attendees")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance")
        }
    }

    override suspend fun validateAbsentDate(
        absentDate: String,
        employeeId: String?,
        attendanceId: String?
    ): ValidationResult {
        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Absent date is required"
            )
        }

        if (employeeId != null) {
            val serverResult = withContext(ioDispatcher) {
                findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)
            }

            if (serverResult) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Selected date already exists.",
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validateAbsentEmployee(employeeId: String): ValidationResult {
        if (employeeId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateIsAbsent(isAbsent: Boolean): ValidationResult {
        if (!isAbsent) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee must be absent."
            )
        }

        return ValidationResult(true)
    }
}
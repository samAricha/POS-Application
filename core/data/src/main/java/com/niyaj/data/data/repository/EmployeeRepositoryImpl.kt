package com.niyaj.data.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.compareSalaryDates
import com.niyaj.common.utils.getSalaryDates
import com.niyaj.common.utils.toError
import com.niyaj.common.utils.toRupee
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.validation.EmployeeValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.PaymentStatus
import com.niyaj.model.filterEmployee
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class EmployeeRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : EmployeeRepository, EmployeeValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Employee Session")
    }

    override suspend fun getAllEmployee(searchText: String): Flow<List<Employee>> {
        return withContext(ioDispatcher) {
            realm
                .query<EmployeeEntity>().sort("employeeId", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { employees ->
                    employees.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterEmployee(searchText) },
                    )
                }
        }
    }

    override suspend fun doesAnyEmployeeExist(): Boolean {
        return withContext(ioDispatcher) {
            realm.query<EmployeeEntity>().find().isNotEmpty()
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

    override suspend fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (employeeId == null) {
                realm.query<EmployeeEntity>("employeeName == $0", employeeName).first().find()
            } else {
                realm.query<EmployeeEntity>(
                    "employeeId != $0 && employeeName == $1",
                    employeeId,
                    employeeName
                ).first().find()
            } != null
        }
    }

    override suspend fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (employeeId == null) {
                realm.query<EmployeeEntity>("employeePhone == $0", employeePhone).first().find()
            } else {
                realm.query<EmployeeEntity>(
                    "employeeId != $0 && employeePhone == $1",
                    employeeId,
                    employeePhone
                ).first().find()
            } != null
        }
    }

    override suspend fun createOrUpdateEmployee(
        newEmployee: Employee,
        employeeId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateEmployeeName(newEmployee.employeeName, employeeId)
                val validatePhone = validateEmployeePhone(newEmployee.employeePhone, employeeId)
                val validatePosition = validateEmployeePosition(newEmployee.employeePosition)
                val validateSalary = validateEmployeeSalary(newEmployee.employeeSalary)

                val validators = listOf(
                    validateName,
                    validatePhone,
                    validatePosition,
                    validateSalary
                )

                if (!validators.any { !it.successful }) {
                    val employee =
                        realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()
                    if (employee != null) {
                        realm.write {
                            findLatest(employee)?.apply {
                                this.employeeName = newEmployee.employeeName
                                this.employeePhone = newEmployee.employeePhone
                                this.employeeType = newEmployee.employeeType.name
                                this.employeeSalary = newEmployee.employeeSalary
                                this.employeeSalaryType = newEmployee.employeeSalaryType.name
                                this.employeePosition = newEmployee.employeePosition
                                this.employeeJoinedDate = newEmployee.employeeJoinedDate
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    } else {
                        realm.write {
                            this.copyToRealm(newEmployee.toEntity())
                        }

                        Resource.Success(true)
                    }
                } else {
                    Resource.Error(validators.toError())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.")
        }
    }

    override suspend fun deleteEmployees(employeeIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                employeeIds.forEach { employeeId ->
                    val employee = realm
                        .query<EmployeeEntity>("employeeId == $0", employeeId)
                        .first()
                        .find()

                    if (employee != null) {
                        realm.write {
                            val salary =
                                this.query<PaymentEntity>(
                                    "employee.employeeId == $0",
                                    employeeId
                                )
                                    .find()
                            val attendance =
                                this.query<AttendanceEntity>(
                                    "employee.employeeId == $0",
                                    employeeId
                                )
                                    .find()

                            if (salary.isNotEmpty()) {
                                delete(salary)
                            }

                            if (attendance.isNotEmpty()) {
                                delete(attendance)
                            }

                            findLatest(employee)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete employee")
        }
    }

    override suspend fun getEmployeeSalaryEstimation(
        employeeId: String,
        selectedDate: Pair<String, String>?
    ): Flow<EmployeeSalaryEstimation> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val employee = withContext(ioDispatcher) {
                        realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()
                    }

                    if (employee != null) {
                        val salaryDate =
                            employee.employeeJoinedDate.let { getSalaryDates(it).first() }

                        val firstDate = selectedDate?.first ?: salaryDate.first
                        val secondDate = selectedDate?.second ?: salaryDate.second

                        val employeeSalary = employee.employeeSalary.toLong()
                        val perDaySalary = employeeSalary.div(30)

                        val absents = withContext(ioDispatcher) {
                            realm.query<AttendanceEntity>(
                                "employee.employeeId == $0 AND absentDate >= $1 AND absentDate <= $2",
                                employeeId,
                                firstDate,
                                secondDate
                            ).find().asFlow()
                        }

                        val payments = withContext(ioDispatcher) {
                            realm.query<PaymentEntity>(
                                "employee.employeeId == $0 AND paymentDate >= $1 AND paymentDate <= $2",
                                employeeId,
                                firstDate,
                                secondDate
                            ).find().asFlow()
                        }

                        payments.combine(absents) { paymentsRes, absentsRes ->
                            val amountPaid = paymentsRes.list.sumOf { it.paymentAmount.toLong() }
                            val noOfPayments = paymentsRes.list.size.toLong()
                            val noOfAbsents = absentsRes.list.size.toLong()

                            PaymentAndAbsentCount(
                                amountPaid,
                                noOfPayments,
                                noOfAbsents
                            )
                        }.collectLatest { (amountPaid, noOfPayments, noOfAbsents) ->
                            val absentSalary = perDaySalary.times(noOfAbsents)
                            val currentSalary = employeeSalary.minus(absentSalary)

                            val status =
                                if (currentSalary >= amountPaid) PaymentStatus.NotPaid else PaymentStatus.Paid

                            val message: String? = if (currentSalary < amountPaid) {
                                "Paid Extra ${
                                    amountPaid.minus(currentSalary).toString().toRupee
                                } Amount"
                            } else if (currentSalary > amountPaid) {
                                "Remaining  ${
                                    currentSalary.minus(amountPaid).toString().toRupee
                                } have to pay."
                            } else null

                            val remainingAmount = currentSalary.minus(amountPaid)

                            send(
                                EmployeeSalaryEstimation(
                                    startDate = firstDate,
                                    endDate = secondDate,
                                    status = status,
                                    message = message,
                                    remainingAmount = remainingAmount.toString(),
                                    paymentCount = noOfPayments.toString(),
                                    absentCount = noOfAbsents.toString()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    override suspend fun getEmployeePayments(employeeId: String): Flow<List<EmployeePayments>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val employee = withContext(ioDispatcher) {
                        realm.query<EmployeeEntity>("employeeId == $0", employeeId)
                            .first()
                            .find()
                    }

                    if (employee != null) {
                        val employeePayments = mutableStateListOf<EmployeePayments>()

                        val joinedDate = employee.employeeJoinedDate
                        val salaryDates =
                            getSalaryDates(joinedDate).filter { joinedDate <= it.first }

                        salaryDates.forEach { date ->
                            val payments = withContext(ioDispatcher) {
                                realm.query<PaymentEntity>(
                                    "employee.employeeId == $0 AND paymentDate >= $1 AND paymentDate <= $2",
                                    employeeId,
                                    date.first,
                                    date.second
                                ).sort("paymentDate", Sort.DESCENDING).find()
                            }.map {
                                it.toExternalModel()
                            }

                            employeePayments.add(
                                EmployeePayments(
                                    startDate = date.first,
                                    endDate = date.second,
                                    payments = payments
                                )
                            )
                        }

                        send(employeePayments.toList())
                    }
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getEmployeeAbsentDates(employeeId: String): Flow<List<EmployeeAbsentDates>> {
        return channelFlow {
            try {
                val employee = withContext(ioDispatcher) {
                    realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()
                }

                if (employee != null) {
                    val employeeAbsentDates = mutableStateListOf<EmployeeAbsentDates>()

                    val joinedDate = employee.employeeJoinedDate
                    val dates = getSalaryDates(joinedDate).filter { joinedDate <= it.first }

                    dates.forEach { date ->
                        val attendances = withContext(ioDispatcher) {
                            realm.query<AttendanceEntity>(
                                "employee.employeeId == $0 AND absentDate >= $1 AND absentDate <= $2",
                                employeeId,
                                date.first,
                                date.second
                            ).sort("absentDate", Sort.DESCENDING)
                                .find()
                                .map { it.absentDate }
                        }

                        employeeAbsentDates.add(
                            EmployeeAbsentDates(
                                startDate = date.first,
                                endDate = date.second,
                                absentDates = attendances
                            )
                        )
                    }

                    send(employeeAbsentDates.toList())
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun getEmployeeMonthlyDate(employeeId: String): List<EmployeeMonthlyDate> {
        return try {
            val monthlyDates = mutableStateListOf<EmployeeMonthlyDate>()
            val employee = withContext(ioDispatcher) {
                realm.query<EmployeeEntity>("employeeId == $0", employeeId).first().find()
            }

            if (employee != null) {
                val joinedDate = employee.employeeJoinedDate
                val dates = getSalaryDates(joinedDate)

                dates.forEach { date ->
                    if (compareSalaryDates(joinedDate, date.first)) {
                        monthlyDates.add(
                            EmployeeMonthlyDate(
                                startDate = date.first,
                                endDate = date.second
                            )
                        )
                    }
                }
            }

            monthlyDates.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun validateEmployeeName(name: String, employeeId: String?): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        if (name.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must be more than 4 characters",
            )
        }

        if (name.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not contain any digit",
            )
        }

        val serverResult = withContext(ioDispatcher) {
            findEmployeeByName(name, employeeId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name already exists.",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun validateEmployeePhone(
        phone: String,
        employeeId: String?
    ): ValidationResult {
        if (phone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not be empty",
            )
        }

        if (phone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must be 10(${phone.length}) digits",
            )
        }

        if (phone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must not contain a letter",
            )
        }

        val result = withContext(ioDispatcher) { findEmployeeByPhone(phone, employeeId) }

        if (result) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no already exists",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateEmployeePosition(position: String): ValidationResult {
        if (position.isEmpty()) {
            return ValidationResult(false, "Employee position is required")
        }

        return ValidationResult(true)
    }

    override fun validateEmployeeSalary(salary: String): ValidationResult {
        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not be empty",
            )
        }

        if (salary.length != 5) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary is in invalid",
            )
        }

        if (salary.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not contain any characters",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}

data class PaymentAndAbsentCount(
    val amountPaid: Long = 0,
    val noOfPayments: Long = 0,
    val noOfAbsents: Long = 0,
)
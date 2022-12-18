package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewEmployee(
    private val employeeRepository: EmployeeRepository
) {

    suspend operator fun invoke(newEmployee: Employee): Resource<Boolean>{
        return employeeRepository.createNewEmployee(newEmployee)
    }
}
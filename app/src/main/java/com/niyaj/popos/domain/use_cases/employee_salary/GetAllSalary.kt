package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.EmployeeSalary
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetAllSalary(private val salaryRepository: SalaryRepository) {

    operator fun invoke(
        searchText: String = "",
    ): Flow<Resource<List<EmployeeSalary>>> {
        return channelFlow {
            salaryRepository.getAllSalary().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { salaries ->
                            salaries.filter { salary ->
                                if (searchText.isNotEmpty()){
                                    salary.employeeSalary.contains(searchText, true) ||
                                    salary.salaryType.contains(searchText, true) ||
                                    salary.salaryGivenDate.contains(searchText, true) ||
                                    salary.salaryPaymentType.contains(searchText, true) ||
                                    salary.salaryNote.contains(searchText, true) ||
                                    salary.createdAt?.contains(searchText, true) == true ||
                                    salary.updatedAt?.contains(searchText, true) == true ||
                                    salary.employee.employeeName.contains(searchText, true) ||
                                    salary.employee.employeePhone.contains(searchText, true) ||
                                    salary.employee.employeeType.contains(searchText, true) ||
                                    salary.employee.employeePosition.contains(searchText, true)
                                }else{
                                    true
                                }
                            }
                        }
                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get data from database"))
                    }
                }
            }
        }
    }

}
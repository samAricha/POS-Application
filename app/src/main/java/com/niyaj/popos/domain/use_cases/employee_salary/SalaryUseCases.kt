package com.niyaj.popos.domain.use_cases.employee_salary

data class SalaryUseCases(
    val getAllSalary: GetAllSalary,
    val getSalaryById: GetSalaryById,
    val getSalaryByEmployeeId: GetSalaryByEmployeeId,
    val addNewSalary: AddNewSalary,
    val updateSalary: UpdateSalary,
    val deleteSalary: DeleteSalary,
    val getEmployeeSalary: GetEmployeeSalary,
    val getSalaryCalculableDate: GetSalaryCalculableDate,
)
package com.niyaj.popos.features.employee_salary.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryValidationRepository
import javax.inject.Inject

class ValidateSalary @Inject constructor(
    private val salaryValidationRepository: SalaryValidationRepository
) {
    operator fun invoke(salary: String): ValidationResult {
        return salaryValidationRepository.validateSalary(salary)
    }
}
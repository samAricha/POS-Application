package com.niyaj.popos.domain.use_cases.product.validation

import com.niyaj.popos.domain.use_cases.product.ProductUseCases
import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateProductName @Inject constructor(
    private val productUseCases: ProductUseCases
) {

    fun execute(productName: String, productId: String? = null): ValidationResult {
        if(productName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name required",
            )
        }

        if(productName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name must be more than 4 characters long"
            )
        }

        val serverResult = productUseCases.findProductByName(productName, productId)

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = "Product name already exists.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}
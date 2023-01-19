package com.niyaj.popos.features.delivery_partner.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerValidationRepository
import javax.inject.Inject

class ValidatePartnerEmail @Inject constructor(
    private val partnerValidationRepository: PartnerValidationRepository
) {

    operator fun invoke(partnerEmail: String, partnerId: String? = null): ValidationResult {
        return partnerValidationRepository.validatePartnerEmail(partnerEmail, partnerId)
    }
}
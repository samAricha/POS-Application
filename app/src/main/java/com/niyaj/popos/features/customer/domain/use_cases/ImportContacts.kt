package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository

class ImportContacts(private val customerRepository: CustomerRepository) {
    suspend operator fun invoke(contacts: List<Contact>): Resource<Boolean>{
        return customerRepository.importContacts(contacts)
    }
}
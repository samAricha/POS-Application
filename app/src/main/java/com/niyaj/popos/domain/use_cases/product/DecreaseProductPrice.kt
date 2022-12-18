package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class DecreaseProductPrice(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(price: Int, productList: List<String> = emptyList()): Resource<Boolean> {
        return productRepository.decreasePrice(price, productList)
    }
}
package com.niyaj.popos.features.product.domain.use_cases

data class ProductUseCases(
    val getAllProducts: GetAllProducts,
    val getProductById: GetProductById,
    val getProductsByCategoryId: GetProductsByCategoryId,
    val findProductByName: FindProductByName,
    val createNewProduct: CreateNewProduct,
    val updateProduct: UpdateProduct,
    val deleteProduct: DeleteProduct,
    val increaseProductPrice: IncreaseProductPrice,
    val decreaseProductPrice: DecreaseProductPrice,
    val importProducts: ImportProducts
)
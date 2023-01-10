package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.util.FilterAddress
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetAllAddress(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(
        filterAddress: FilterAddress = FilterAddress.ByAddressId(SortType.Ascending),
        searchText :String =  ""
    ): Flow<Resource<List<Address>>>{
        return channelFlow {
            addressRepository.getAllAddress().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { addresses ->
                            when(filterAddress.sortType){
                                SortType.Ascending -> {
                                    when(filterAddress){
                                        is FilterAddress.ByAddressId -> { addresses.sortedBy { it.addressId } }
                                        is FilterAddress.ByShortName -> { addresses.sortedBy { it.shortName } }
                                        is FilterAddress.ByAddressName -> { addresses.sortedBy { it.addressName } }
                                        is FilterAddress.ByAddressDate -> { addresses.sortedBy { it.createdAt } }
                                    }
                                }
                                SortType.Descending -> {
                                    when(filterAddress){
                                        is FilterAddress.ByAddressId -> { addresses.sortedByDescending { it.addressId } }
                                        is FilterAddress.ByShortName -> { addresses.sortedByDescending { it.shortName } }
                                        is FilterAddress.ByAddressName -> { addresses.sortedByDescending { it.addressName } }
                                        is FilterAddress.ByAddressDate -> { addresses.sortedByDescending { it.createdAt } }
                                    }
                                }
                            }.filter { address ->
                                if(searchText.isNotEmpty()){
                                    address.shortName.contains(searchText, true) ||
                                            address.addressName.contains(searchText, true)
                                }else {
                                    true
                                }
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get addresses from repository"))
                    }
                }
            }
        }
    }
}
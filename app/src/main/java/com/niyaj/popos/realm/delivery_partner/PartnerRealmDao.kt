package com.niyaj.popos.realm.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PartnerRealmDao {

    suspend fun getAllPartner(): Flow<Resource<List<PartnerRealm>>>

    suspend fun getPartnerById(partnerId: String): Resource<PartnerRealm?>

    suspend fun getPartnerByEmail(partnerEmail: String, partnerId: String? = null): Resource<Boolean>

    suspend fun getPartnerByPhone(partnerPhone: String, partnerId: String? = null): Resource<Boolean>

    suspend fun createNewPartner(newPartner: DeliveryPartner): Resource<Boolean>

    suspend fun updatePartner(newPartner: DeliveryPartner, partnerId: String): Resource<Boolean>

    suspend fun deletePartner(partnerId: String): Resource<Boolean>
}
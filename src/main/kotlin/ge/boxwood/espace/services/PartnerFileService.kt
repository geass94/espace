package ge.boxwood.espace.services

import ge.boxwood.espace.models.Partner
import ge.boxwood.espace.models.PartnerFile

interface PartnerFileService {
    fun create(base64File: String, partner: Partner): PartnerFile
    fun update(): PartnerFile
    fun delete(): Void
    fun getOne(id: Long?): PartnerFile
    fun getByPartner(property: Partner): List<PartnerFile>
    fun getOneBytes(id: Long): ByteArray
}
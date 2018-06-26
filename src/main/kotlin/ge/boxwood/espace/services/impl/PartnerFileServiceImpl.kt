package ge.boxwood.espace.services.impl

import ge.boxwood.espace.models.Partner
import ge.boxwood.espace.models.PartnerFile
import ge.boxwood.espace.models.enums.Status
import ge.boxwood.espace.repositories.PartnerFileRepository
import ge.boxwood.espace.services.PartnerFileService
import ge.boxwood.espace.services.StorageService
import org.springframework.stereotype.Service
import sun.misc.BASE64Decoder
import java.util.*

@Service
class PartnerFileServiceImpl(val storageService: StorageService,
                             val partnerFileRepository: PartnerFileRepository): PartnerFileService {
    override fun create(base64File: String, partner: Partner): PartnerFile {
        var prtFiles: List<PartnerFile> = partnerFileRepository.findAllByPartner(partner)
        for(prtFile in prtFiles)
        {
            prtFile.status = Status.DELETED
            partnerFileRepository.save(prtFile)
        }
        val decoder = BASE64Decoder()
//        var imageByte = decoder.decodeBuffer(base64File.split(",").get(1))
        var imageByte = decoder.decodeBuffer(base64File)
        var partnerFile = PartnerFile()
        partnerFile.status = Status.ACTIVE
        partnerFile.partner = partner
        partnerFile.createdBy = partner.id
        partnerFile.extension = "image/jpeg"
        partnerFile.fileName = UUID.randomUUID().toString()
        var storageUUID = storageService.create(imageByte)
        partnerFile.uuid = storageUUID
        partnerFile = partnerFileRepository.save(partnerFile)
        return partnerFile
    }

    override fun update(): PartnerFile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOne(id: Long?): PartnerFile {
        return partnerFileRepository.findOne(id)
    }

    override fun getByPartner(property: Partner): List<PartnerFile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOneBytes(id: Long): ByteArray {
        return storageService.getBytes(partnerFileRepository.findOne(id).uuid)
    }
}
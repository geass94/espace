package ge.boxwood.espace.services.impl

import ge.boxwood.espace.models.User
import ge.boxwood.espace.models.UserFile
import ge.boxwood.espace.models.enums.Status
import ge.boxwood.espace.repositories.UserFileRepostiory
import ge.boxwood.espace.services.StorageService
import ge.boxwood.espace.services.UserFileService
import org.springframework.stereotype.Service
import sun.misc.BASE64Decoder
import java.util.*

@Service
class UserFileServiceImpl(val storageService: StorageService,
                          val userFileRepostiory: UserFileRepostiory) : UserFileService {
    override fun create(base64File: String, user: User): UserFile {
        var usrFiles: List<UserFile> = userFileRepostiory.findAllByUser(user)
        for(usrFile in usrFiles)
        {
            usrFile.status = Status.DELETED
            userFileRepostiory.save(usrFile)
        }

        val decoder = BASE64Decoder()
//        var imageByte = decoder.decodeBuffer(base64File.split(",").get(1))
        var imageByte = decoder.decodeBuffer(base64File)
        var userFile = UserFile()
        userFile.status = Status.ACTIVE
        userFile.user = user
        userFile.createdBy = user.id
        userFile.extension = "image/jpeg"
        userFile.fileName = UUID.randomUUID().toString()
        var storageUUID = storageService.create(imageByte)
        userFile.uuid = storageUUID
        userFile = userFileRepostiory.save(userFile)
        return userFile
    }

    override fun update(): UserFile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOne(id: Long?): UserFile {
        return userFileRepostiory.findOne(id)
    }

    override fun getByUser(user: User): List<UserFile> {
        return userFileRepostiory.findAllByUser(user)
    }

    override fun getOneBytes(id: Long): ByteArray {
        return storageService.getBytes(userFileRepostiory.findOne(id).uuid)
    }
}
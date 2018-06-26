package ge.boxwood.espace.services

import ge.boxwood.espace.models.User
import ge.boxwood.espace.models.UserFile

interface UserFileService {
    fun create(base64File: String, user: User): UserFile
    fun update(): UserFile
    fun delete(): Void
    fun getOne(id: Long?): UserFile
    fun getByUser(property: User): List<UserFile>
    fun getOneBytes(id: Long): ByteArray
}
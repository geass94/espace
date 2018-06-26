package ge.boxwood.espace.services.impl

import ge.boxwood.espace.config.utils.ImageUtils
import ge.boxwood.espace.services.StorageService
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@Service
class StorageServiceImpl(val imageUtils: ImageUtils) : StorageService {
    @Value("\${espace.storage.directory}")
    private val storageFolder: String? = null

    override fun create(byteFile: ByteArray): String {
        val uuid = UUID.randomUUID().toString()

        val stream = ByteArrayInputStream(imageUtils.scale(byteFile, 250, 250))

        Files.copy(stream, Paths.get(this.storageFolder).resolve(uuid))
        return uuid
    }

    override fun getBytes(uuid: String): ByteArray {
        try {
            val path = Paths.get(storageFolder + uuid)
            return Files.readAllBytes(path)
        } catch (e: Exception) {
            e.printStackTrace()
            return byteArrayOf()
        }
    }

    public fun getBase64String(multipartFile: MultipartFile): String {
        return Base64.encodeBase64String(multipartFile.bytes)
    }

}
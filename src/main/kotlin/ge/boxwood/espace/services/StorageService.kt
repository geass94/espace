package ge.boxwood.espace.services

interface StorageService {
    fun create(byteFile: ByteArray): String
    fun getBytes(uuid: String): ByteArray
}
package ge.boxwood.espace.controllers

import ge.boxwood.espace.services.PartnerFileService
import ge.boxwood.espace.services.StorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/partner-files")
class PartnerFileController(val partnerFileService: PartnerFileService,
                            val storageService: StorageService) {
    @GetMapping("/{id}")
    open fun get(response: HttpServletResponse, @PathVariable("id") id: Long){
        val userFile = partnerFileService.getOne(id)
        if (userFile == null){
            throw RuntimeException("File was not found")
        }
        response.contentType = userFile.extension
        response.setHeader("filename", userFile.name)
        response.setHeader("Content-disposition", "attachment; filename=" + userFile.name)
        response.outputStream.write(partnerFileService.getOneBytes(id))
        response.flushBuffer()
    }
}
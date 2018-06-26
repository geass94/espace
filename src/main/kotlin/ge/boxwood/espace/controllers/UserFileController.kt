package ge.boxwood.espace.controllers

import ge.boxwood.espace.services.StorageService
import ge.boxwood.espace.services.UserFileService
import ge.boxwood.espace.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/user-files")
class UserFileController(val userFileService: UserFileService,
                         val storageService: StorageService,
                         val userService: UserService) {
    @GetMapping("/{id}")
    open fun get(response: HttpServletResponse, @PathVariable("id") id: Long){
        val userFile = userFileService.getOne(id)
        if (userFile == null){
            throw RuntimeException("File was not found")
        }
        response.contentType = userFile.extension
        response.setHeader("filename", userFile.name)
        response.setHeader("Content-disposition", "attachment; filename=" + userFile.name)
        response.outputStream.write(userFileService.getOneBytes(id))
        response.flushBuffer()
    }
}
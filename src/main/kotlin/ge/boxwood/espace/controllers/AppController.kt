package ge.boxwood.espace.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController {

    @GetMapping("")
    fun checkAvailability(): ResponseEntity<Any>{

        return ResponseEntity.ok("OK")
    }
}
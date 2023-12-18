package cz.cvut.fit.tjv.cs_skin_system.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
            e: ConstraintViolationException
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }
}

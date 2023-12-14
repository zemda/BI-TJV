package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.CsgoCaseService
import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/csgoCase")
@CrossOrigin(origins = ["http://localhost:5173"])
class CsgoCaseController(private val csgoCaseService: CsgoCaseService) {

    @GetMapping("/{id}")
    @Operation(summary = "Get a CSGO case by its id")
    fun getCsgoCaseById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val csgoCase = csgoCaseService.getById(id)
            ResponseEntity(csgoCase, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Case not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    fun getCsgoCases(): ResponseEntity<List<CsgoCase>> {
        return ResponseEntity.ok(csgoCaseService.getAll())
    }

    @PostMapping
    @Operation(summary = "Create a CSGO case")
    fun createCsgoCase(@RequestBody csgoCase: CsgoCase): ResponseEntity<Any> {
        return try {
            val createdCsgoCase = csgoCaseService.create(csgoCase)
            ResponseEntity(createdCsgoCase, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message ?: "Case name already exists", HttpStatus.CONFLICT)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/{caseId}/changePrice/{newPrice}")
    @Operation(summary = "Change price of a CSGO case")
    fun changePrice(@PathVariable caseId: Long, @PathVariable newPrice: Double): ResponseEntity<Any> {
        return try {
            val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, newPrice)
            ResponseEntity(updatedCsgoCase, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "CSGO Case not found", HttpStatus.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message ?: "Invalid new price", HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/{caseId}/addSkins")
    @Operation(summary = "Add skins to a CSGO case")
    fun addSkinsToCase(@PathVariable caseId: Long, @RequestBody skinIds: List<Long>,
                       @RequestParam addSkins: Boolean): ResponseEntity<Any> {
        return try {
            val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, skinIds, addSkins)
            ResponseEntity(updatedCsgoCase, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            when {
                e.message?.contains("case") ?: false -> ResponseEntity(e.message ?: "CSGO Case not found", HttpStatus.NOT_FOUND)
                e.message?.contains("skin") ?: false -> ResponseEntity(e.message ?: "Skin not found", HttpStatus.NOT_FOUND)
                else -> ResponseEntity(e.message ?: "Item not found", HttpStatus.NOT_FOUND)
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message ?: "Illegal state detected", HttpStatus.CONFLICT)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a CSGO case")
    fun deleteCsgoCase(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            csgoCaseService.deleteById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "CSGO Case not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
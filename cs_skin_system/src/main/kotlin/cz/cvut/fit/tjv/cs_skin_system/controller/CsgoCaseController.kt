package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.CsgoCaseService
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/csgoCase")
@CrossOrigin(origins = ["http://localhost:5173"])
class CsgoCaseController(private val csgoCaseService: CsgoCaseService) : CsgoCaseControllerInterface{

    override fun getCsgoCaseById(@PathVariable id: Long): ResponseEntity<Any> {
        val csgoCase = csgoCaseService.getById(id)
        return ResponseEntity(csgoCase, HttpStatus.OK)
    }

    override fun getCsgoCases(): ResponseEntity<List<CsgoCaseDTO>> {
        return ResponseEntity.ok(csgoCaseService.getAll())
    }

    override fun createCsgoCase(@RequestBody csgoCase: CsgoCaseCreateDTO): ResponseEntity<Any> {
        val createdCsgoCase = csgoCaseService.create(csgoCase)
        return ResponseEntity(createdCsgoCase, HttpStatus.CREATED)
    }

    override fun changePrice(
            @PathVariable caseId: Long,
            @PathVariable newPrice: Double
    ): ResponseEntity<Any> {
        val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, newPrice)
        return ResponseEntity(updatedCsgoCase, HttpStatus.OK)
    }

    override fun addSkinsToCase(
            @PathVariable caseId: Long,
            @RequestBody skinIds: List<Long>,
            @RequestParam addSkins: Boolean
    ): ResponseEntity<Any> {
        val updatedCsgoCase = csgoCaseService.updateCsgoCase(caseId, skinIds, addSkins)
        return ResponseEntity(updatedCsgoCase, HttpStatus.OK)
    }

    override fun deleteCsgoCase(@PathVariable id: Long): ResponseEntity<Any> {
        csgoCaseService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

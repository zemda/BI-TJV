package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/weapons")
@CrossOrigin(origins = ["http://localhost:5173"])
class WeaponController(val weaponService: WeaponService) {

    @GetMapping("/{id}")
    @Operation(summary = "Get a weapon by its id")
    fun getWeaponById(@PathVariable id: Long): ResponseEntity<Any> {
        val weapon = weaponService.getById(id)
        return ResponseEntity(weapon, HttpStatus.OK)
    }

    @GetMapping
    @Operation(summary = "Fetch all skins")
    fun getWeapons(): ResponseEntity<List<WeaponDTO>> {
        val weapons = weaponService.getAll()
        return ResponseEntity.ok(weapons)
    }

    @PostMapping
    @Operation(summary = "Create a weapon")
    fun createWeapon(@RequestBody weapon: WeaponCreateDTO): ResponseEntity<Any> {
        val createdWeapon = weaponService.create(weapon)
        return ResponseEntity(createdWeapon, HttpStatus.CREATED)
    }

    @PutMapping("/{id}/tag")
    @Operation(summary = "Update tag of a weapon with given id")
    fun updateWeaponTag(@PathVariable id: Long, @RequestParam newTag: String): ResponseEntity<Any> {
        val updatedWeapon = weaponService.updateWeaponTag(id, newTag)
        return ResponseEntity(updatedWeapon, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete given weapon")
    fun deleteWeapon(@PathVariable id: Long): ResponseEntity<Any> {
        weaponService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

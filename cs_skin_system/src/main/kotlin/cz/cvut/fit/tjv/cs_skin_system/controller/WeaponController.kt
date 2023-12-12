package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/weapons")
@CrossOrigin(origins = ["http://localhost:5173"])
class WeaponController (val weaponService: WeaponService){

    @GetMapping("/{id}")
    @Operation(summary = "Get a weapon by its id")
    fun getWeaponById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val weapon = weaponService.getWeaponById(id)
            ResponseEntity(weapon, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Weapon not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    @Operation(summary = "Fetch all skins")
    fun getWeapons(): ResponseEntity<List<Weapon>> {
        val weapons = weaponService.getWeapons()
        return ResponseEntity.ok(weapons)
    }

    @PostMapping
    @Operation(summary = "Create a weapon")
    fun createWeapon(@RequestBody weapon: Weapon, @RequestParam skinId: Long): ResponseEntity<Any> {
        return try {
            val createdWeapon = weaponService.createWeapon(weapon, skinId)
            ResponseEntity(createdWeapon, HttpStatus.CREATED)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Invalid skinId", HttpStatus.NOT_FOUND)
        } catch (e: IllegalStateException) {
            ResponseEntity(e.message ?: "This skin is already on a weapon", HttpStatus.CONFLICT)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/{id}/tag")
    @Operation(summary = "Update tag of a weapon with given id")
    fun updateWeaponTag(@PathVariable id: Long, @RequestParam newTag: String): ResponseEntity<Any> {
        return try {
            val updatedWeapon = weaponService.updateWeaponTag(id, newTag)
            ResponseEntity(updatedWeapon, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Weapon not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(e.message ?: "Server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete a weapon by its id")
    fun deleteWeapon(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            weaponService.deleteWeapon(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: NoSuchElementException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
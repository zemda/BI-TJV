package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/weapons")
@CrossOrigin(origins = ["http://localhost:5173"])
class WeaponController(val weaponService: WeaponService) : WeaponControllerInterface{

    override fun getWeaponById(@PathVariable id: Long): ResponseEntity<Any> {
        val weapon = weaponService.getById(id)
        return ResponseEntity(weapon, HttpStatus.OK)
    }

    override fun getWeapons(): ResponseEntity<List<WeaponDTO>> {
        val weapons = weaponService.getAll()
        return ResponseEntity.ok(weapons)
    }

    override fun createWeapon(@RequestBody weapon: WeaponCreateDTO): ResponseEntity<Any> {
        val createdWeapon = weaponService.create(weapon)
        return ResponseEntity(createdWeapon, HttpStatus.CREATED)
    }

    override fun updateWeaponTag(@PathVariable id: Long, @RequestParam newTag: String): ResponseEntity<Any> {
        val updatedWeapon = weaponService.updateWeaponTag(id, newTag)
        return ResponseEntity(updatedWeapon, HttpStatus.OK)
    }

   override fun deleteWeapon(@PathVariable id: Long): ResponseEntity<Any> {
        weaponService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

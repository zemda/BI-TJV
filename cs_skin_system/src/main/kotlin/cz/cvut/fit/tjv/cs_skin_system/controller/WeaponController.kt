package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/weapons")
@CrossOrigin(origins = ["http://localhost:5173"])
class WeaponController (val weaponService: WeaponService){

    @GetMapping("/{id}")
    fun getWeaponById(@PathVariable id: Long): ResponseEntity<Weapon> {
        val weapon = weaponService.getWeaponById(id)
        return ResponseEntity.ok(weapon)
    }

    @GetMapping
    fun getWeapons(): ResponseEntity<List<Weapon>> {
        val weapons = weaponService.getWeapons()
        return ResponseEntity.ok(weapons)
    }

    @PostMapping
    fun createWeapon(@RequestBody weapon: Weapon, @RequestParam(required = false) skinId: Long?): ResponseEntity<Weapon> {
        val createdWeapon = skinId?.let { weaponService.createWeapon(weapon, it) }
        return ResponseEntity.ok(createdWeapon)
    }

    @PutMapping("/{id}/tag")
    fun updateWeaponTag(@PathVariable id: Long, @RequestParam newTag: String): ResponseEntity<Weapon> {
        val updatedWeapon = weaponService.updateWeaponTag(id, newTag)
        return ResponseEntity.ok(updatedWeapon)
    }

    @DeleteMapping
    fun deleteWeapon(@RequestBody weapon: Weapon): ResponseEntity<Void> {
        weaponService.deleteWeapon(weapon.id)
        return ResponseEntity.ok().build()
    }
}
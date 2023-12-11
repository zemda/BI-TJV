package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/skins")
@CrossOrigin(origins = ["http://localhost:5173"])
class SkinController (val skinService: SkinService){

    @GetMapping("/{id}")
    fun getSkinById(@PathVariable id: Long): ResponseEntity<Skin> {
        val skin = skinService.getSkinById(id)
        return ResponseEntity.ok(skin)
    }

    @GetMapping
    fun getSkins(): ResponseEntity<List<Skin>> {
        val skins = skinService.getSkins()
        return ResponseEntity.ok(skins)
    }

    @PostMapping
    fun createSkin(@RequestBody skin: Skin, @RequestParam(required = false) caseId: Long?): ResponseEntity<Skin> {
        val createdSkin = skinService.createSkin(skin, caseId)
        return ResponseEntity.ok(createdSkin)
    }

    @PutMapping("/{id}/price")
    fun updateSkinPrice(@PathVariable id: Long, @RequestParam newPrice: Double): ResponseEntity<Skin> {
        val updatedSkin = skinService.updateSkinPrice(id, newPrice)
        return ResponseEntity.ok(updatedSkin)
    }

    @DeleteMapping
    fun deleteSkin(@RequestBody skin: Skin): ResponseEntity<Void> {
        skinService.deleteSkin(skin.id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/valuable")
    fun getValuableSkins(
        @RequestParam rarity: String,
        @RequestParam price: Double,
        @RequestParam caseName: String,
        @RequestParam weapon: String): ResponseEntity<List<Skin>> {

        val valuableSkins = skinService.getValuableSkins(rarity, price, caseName, weapon)
        return ResponseEntity.ok(valuableSkins)
    }

    @GetMapping("/noWeapon")
    fun getSkinsWithNoWeapon(): ResponseEntity<List<Skin>> {
        val skins = skinService.getSkinsWithNoWeapon()
        return ResponseEntity.ok(skins)
    }

}
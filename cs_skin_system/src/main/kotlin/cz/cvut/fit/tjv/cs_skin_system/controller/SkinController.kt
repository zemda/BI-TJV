package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/skins")
@CrossOrigin(origins = ["http://localhost:5173"])
class SkinController (val skinService: SkinService){

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a skin by its id")
    fun getSkinById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val skin = skinService.getSkinById(id)
            ResponseEntity(skin, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Skin not found", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping
    @Operation(summary = "Fetch all skins")
    fun getSkins(): ResponseEntity<List<Skin>> {
        val skins = skinService.getSkins()
        return ResponseEntity.ok(skins)
    }

    @PostMapping
    @Operation(summary = "Create and optionally assign it a case")
    fun createSkin(@RequestBody skin: Skin, @RequestParam(required = false) caseId: Long?): ResponseEntity<Any> {
        return try {
            val createdSkin = skinService.createSkin(skin, caseId)
            ResponseEntity(createdSkin, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message ?: "Skin already exists.", HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/{id}/price")
    @Operation(summary = "Update price to a skin with given id")
    fun updateSkinPrice(@PathVariable id: Long, @RequestParam newPrice: Double): ResponseEntity<Any> {
        return try {
            val updatedSkin = skinService.updateSkinPrice(id, newPrice)
            ResponseEntity(updatedSkin, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Skin not found", HttpStatus.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(e.message ?: "Invalid new price", HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete given skin")
    fun deleteSkin(@RequestBody skin: Skin): ResponseEntity<Any> {
        return try {
            skinService.deleteSkin(skin.id)
            ResponseEntity.ok().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message ?: "Skin not found", HttpStatus.NOT_FOUND)
        } catch (e: IllegalStateException) {
            ResponseEntity(e.message ?: "Skin is on a weapon, delete that first", HttpStatus.CONFLICT)
        }
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
    @Operation(summary = "Fetch skins that we can apply to a weapon")
    fun getSkinsWithNoWeapon(): ResponseEntity<List<Skin>> {
        val skins = skinService.getSkinsWithNoWeapon()
        return ResponseEntity.ok(skins)
    }

    @GetMapping("/filter")
    @Operation(summary = "Fetch skins that meet the parameters")
    fun filterSkins(@RequestParam(required = false) skinId: Long?,
                    @RequestParam(required = false) name: String?,
                    @RequestParam(required = false) rarity: String?,
                    @RequestParam(required = false) exterior: String?,
                    @RequestParam(required = false) price: Double?,
                    @RequestParam(required = false) paintSeed: Int?,
                    @RequestParam(required = false) float: Double?,
                    @RequestParam(required = false) weaponId: Long?,
                    @RequestParam(required = false) weaponName: String?,
                    @RequestParam(required = false) csgoCaseId: Long?,
                    @RequestParam(required = false) csgoCaseName: String?):  ResponseEntity<List<Skin>> {

        val result = skinService.filterSkins(skinId, name, rarity, exterior, price, paintSeed, float, weaponId, weaponName, csgoCaseId, csgoCaseName)
        return ResponseEntity.ok(result)
    }

}
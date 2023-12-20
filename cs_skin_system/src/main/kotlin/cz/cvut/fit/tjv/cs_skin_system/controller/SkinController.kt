package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/skins")
@CrossOrigin(origins = ["http://localhost:5173"])
class SkinController(val skinService: SkinService) {

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a skin by its id")
    fun getSkinById(@PathVariable id: Long): ResponseEntity<Any> {
        val skin = skinService.getById(id)
        return ResponseEntity(skin, HttpStatus.OK)
    }

    @GetMapping
    @Operation(summary = "Fetch all skins")
    fun getSkins(): ResponseEntity<List<SkinDTO>> {
        val skins = skinService.getAll()
        return ResponseEntity.ok(skins)
    }

    @PostMapping
    @Operation(summary = "Create and optionally assign it a case")
    fun createSkin(@RequestBody skin: SkinCreateDTO): ResponseEntity<Any> {
        val createdSkin = skinService.create(skin)
        return ResponseEntity(createdSkin, HttpStatus.CREATED)
    }

    @PutMapping("/{id}/price")
    @Operation(summary = "Update price to a skin with given id")
    fun updateSkinPrice(
            @PathVariable id: Long,
            @RequestParam newPrice: Double
    ): ResponseEntity<Any> {
        val updatedSkin = skinService.updateSkinPrice(id, newPrice)
        return ResponseEntity(updatedSkin, HttpStatus.OK)
    }

    @PutMapping("/{id}/cases")
    @Operation(summary = "Update from which cases can skin drop")
    fun updateSkinDropsFrom(
            @PathVariable id: Long,
            @RequestBody caseIds: List<Long>
    ): ResponseEntity<Any> {
        val updatedSkin = skinService.updateSkinDropsFrom(id, caseIds)
        return ResponseEntity(updatedSkin, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete given skin")
    fun deleteSkin(@PathVariable id: Long): ResponseEntity<Any> {
        skinService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/noWeapon")
    @Operation(summary = "Fetch skins that we can apply to a weapon")
    fun getSkinsWithNoWeapon(): ResponseEntity<List<SkinDTO>> {
        val skins = skinService.getSkinsWithNoWeapon()
        return ResponseEntity.ok(skins)
    }

    @GetMapping("/filter")
    @Operation(summary = "Fetch skins that meet the parameters")
    fun filterSkins(
            @RequestParam(required = false) skinId: Long?,
            @RequestParam(required = false) name: String?,
            @RequestParam(required = false) rarity: String?,
            @RequestParam(required = false) exterior: String?,
            @RequestParam(required = false) price: Double?,
            @RequestParam(required = false) paintSeed: Int?,
            @RequestParam(required = false) float: Double?,
            @RequestParam(required = false) weaponId: Long?,
            @RequestParam(required = false) weaponName: String?,
            @RequestParam(required = false) csgoCaseId: Long?,
            @RequestParam(required = false) csgoCaseName: String?
    ): ResponseEntity<List<SkinDTO>> {

        val result =
                skinService.filterSkins(
                        skinId,
                        name,
                        rarity,
                        exterior,
                        price,
                        paintSeed,
                        float,
                        weaponId,
                        weaponName,
                        csgoCaseId,
                        csgoCaseName
                )
        return ResponseEntity.ok(result)
    }
}

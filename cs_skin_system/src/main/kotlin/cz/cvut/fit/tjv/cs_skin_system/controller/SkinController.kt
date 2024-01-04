package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/skins")
@CrossOrigin(origins = ["http://localhost:5173"])
class SkinController(val skinService: SkinService) : SkinControllerInterface {

    override fun getSkinById(@PathVariable id: Long): ResponseEntity<Any> {
        val skin = skinService.getById(id)
        return ResponseEntity(skin, HttpStatus.OK)
    }

    override fun getSkins(): ResponseEntity<List<SkinDTO>> {
        val skins = skinService.getAll()
        return ResponseEntity.ok(skins)
    }

    override fun createSkin(@RequestBody skin: SkinCreateDTO): ResponseEntity<Any> {
        val createdSkin = skinService.create(skin)
        return ResponseEntity(createdSkin, HttpStatus.CREATED)
    }

    override fun updateSkinPrice(
            @PathVariable id: Long,
            @RequestParam newPrice: Double
    ): ResponseEntity<Any> {
        val updatedSkin = skinService.updateSkinPrice(id, newPrice)
        return ResponseEntity(updatedSkin, HttpStatus.OK)
    }

    override fun updateSkinDropsFrom(
            @PathVariable id: Long,
            @RequestBody caseIds: List<Long>
    ): ResponseEntity<Any> {
        val updatedSkin = skinService.updateSkinDropsFrom(id, caseIds)
        return ResponseEntity(updatedSkin, HttpStatus.OK)
    }

    override fun deleteSkin(@PathVariable id: Long): ResponseEntity<Any> {
        skinService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getSkinsWithNoWeapon(): ResponseEntity<List<SkinDTO>> {
        val skins = skinService.getSkinsWithNoWeapon()
        return ResponseEntity.ok(skins)
    }

    override fun skinExists(skinId: Long, weaponName: String): ResponseEntity<Boolean> {
        print(skinService.existsSkin(skinId, weaponName))
        return ResponseEntity.ok(skinService.existsSkin(skinId, weaponName))
    }

    override fun getCasesForSkin(@PathVariable skinId: Long): ResponseEntity<List<CsgoCaseDTO>> {
        val cases = skinService.getCasesForSkin(skinId)
        return ResponseEntity.ok(cases)
    }

    override fun filterSkins(
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

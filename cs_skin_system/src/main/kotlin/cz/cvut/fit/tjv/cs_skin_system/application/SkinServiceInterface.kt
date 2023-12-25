package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO

interface SkinServiceInterface {
    fun updateSkinPrice(skinId: Long, newPrice: Double): SkinDTO
    fun updateSkinDropsFrom(skinId: Long, caseIds: List<Long>): SkinDTO
    fun filterSkins(
            skinId: Long?,
            name: String?,
            rarity: String?,
            exterior: String?,
            price: Double?,
            paintSeed: Int?,
            float: Double?,
            weaponId: Long?,
            weaponName: String?,
            csgoCaseId: Long?,
            csgoCaseName: String?
    ): List<SkinDTO>
    fun getSkinsWithNoWeapon(): List<SkinDTO>
    fun getCasesForSkin(skinId: Long): List<CsgoCaseDTO>
}

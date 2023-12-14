package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin

interface SkinServiceInterface {

    fun updateSkinPrice(skinId: Long, newPrice: Double) : Skin

    fun updateSkinDropsFrom(skinId: Long, caseIds: List<Long>) : Skin


    fun filterSkins(skinId: Long?, name: String?, rarity: String?, exterior: String?,
                    price: Double?, paintSeed: Int?, float: Double?,
                    weaponId: Long?, weaponName: String?, csgoCaseId: Long?, csgoCaseName: String?):
            List<Skin>

    fun getSkinsWithNoWeapon(): List<Skin>
}
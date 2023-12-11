package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin

interface SkinServiceInterface {

    fun getSkinById(id: Long) : Skin

    fun getSkins() : List<Skin>

    fun createSkin(skin: Skin, caseId: Long?) : Skin

    fun updateSkinPrice(skinId: Long, newPrice: Double) : Skin

    fun deleteSkin(skinId: Long)

    fun getValuableSkins(rarity: String, price: Double, caseName: String, weapon: String): List<Skin>

    fun getSkinsWithNoWeapon(): List<Skin>
}
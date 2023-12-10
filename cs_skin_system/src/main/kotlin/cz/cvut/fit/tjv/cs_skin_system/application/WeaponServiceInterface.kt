package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon

interface WeaponServiceInterface {

    fun getWeaponById(id: Long) : Weapon

    fun getWeapons() : List<Weapon>

    fun createWeapon(weapon: Weapon, skinId: Long) : Weapon

    fun updateWeaponTag(weaponId: Long, newTag: String) : Weapon

    fun deleteWeapon(weaponId: Long)
}
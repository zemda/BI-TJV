package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon

interface WeaponServiceInterface {

    fun updateWeaponTag(weaponId: Long, newTag: String) : Weapon

}
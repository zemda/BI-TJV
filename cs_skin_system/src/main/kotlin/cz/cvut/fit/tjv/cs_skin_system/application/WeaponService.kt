package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPAWeaponRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.NoSuchElementException

@Service
@Transactional
class WeaponService (var weaponRepo : JPAWeaponRepository,
                     var skinRepo: JPASkinRepository
                    ) : WeaponServiceInterface{

    override fun getWeaponById(id: Long): Weapon {
        return weaponRepo.findById(id).orElseThrow{
            NoSuchElementException("Weapon with id $id not found.")
        }
    }

    override fun getWeapons(): List<Weapon> {
        return weaponRepo.findAll()
    }

    override fun updateWeaponTag(weaponId: Long, newTag: String): Weapon {
        val existingWeapon = weaponRepo.findById(weaponId)
            .orElseThrow { NoSuchElementException("No weapon with id $weaponId") }

        existingWeapon.tag = newTag

        return weaponRepo.save(existingWeapon)
    }

    override fun createWeapon(weapon: Weapon, skinId: Long): Weapon {
        val skin = skinRepo.findById(skinId).orElseThrow {
            NoSuchElementException("Invalid skinId.")
        }
        if (skin.weapon != null){
            throw IllegalStateException("This skin is already on a weapon.")
        }
        weapon.skin = skin

        val createdWeapon = weaponRepo.save(weapon)
        skin.weapon = createdWeapon

        skinRepo.save(skin)

        return createdWeapon
    }


    override fun deleteWeapon(weaponId: Long) {
        if (weaponRepo.existsById(weaponId)) {
            val weapon = getWeaponById(weaponId)
            weaponRepo.delete(weapon)
        }else{
            throw NoSuchElementException("No weapon with id $weaponId.")
        }
    }

}

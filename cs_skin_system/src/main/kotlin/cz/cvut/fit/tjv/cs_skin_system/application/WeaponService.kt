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
                    ) : WeaponServiceInterface, CrudServiceInterface<Weapon, Long> {

    override fun getById(id: Long): Weapon {
        return weaponRepo.findById(id).orElseThrow{
            NoSuchElementException("Weapon with id $id not found.")
        }
    }

    override fun getAll(): List<Weapon> {
        return weaponRepo.findAll()
    }

    /**
     * Creates a new Weapon entity and saves it to the database.
     * SkinId must be provided, the method adds virtual relation between the Weapon and Skin
     *
     * @param entity The Weapon entity to be created.
     * @param opt The ID of the skin with which is weapon painted, this parameter is not optional here
     * @return The created Weapon entity.
     */
    override fun create(entity: Weapon, opt: Long?): Weapon {
        if (opt == null) {
            throw IllegalArgumentException("A skinId must be provided.")
        }
        val skin = skinRepo.findById(opt).orElseThrow {
            NoSuchElementException("Invalid skinId.")
        }
        if (skin.weapon != null){
            throw IllegalStateException("This skin is already on a weapon.")
        }
        entity.skin = skin

        val createdWeapon = weaponRepo.save(entity)
        skin.weapon = createdWeapon

        skinRepo.save(skin)

        return createdWeapon
    }

    override fun deleteById(id: Long) {
        if (weaponRepo.existsById(id)) {
            val weapon = getById(id)
            weapon.skin?.weapon = null
            weaponRepo.delete(weapon)
        }else{
            throw NoSuchElementException("No weapon with id $id.")
        }
    }

    override fun updateWeaponTag(weaponId: Long, newTag: String): Weapon {
        val existingWeapon = weaponRepo.findById(weaponId)
            .orElseThrow { NoSuchElementException("No weapon with id $weaponId") }

        existingWeapon.tag = newTag

        return weaponRepo.save(existingWeapon)
    }
}

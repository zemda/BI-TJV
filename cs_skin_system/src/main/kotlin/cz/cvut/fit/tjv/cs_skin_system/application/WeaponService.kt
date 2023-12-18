package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPAWeaponRepository
import jakarta.transaction.Transactional
import java.lang.IllegalStateException
import java.util.NoSuchElementException
import org.springframework.stereotype.Service

@Service
@Transactional
class WeaponService(var weaponRepo: JPAWeaponRepository, var skinRepo: JPASkinRepository) :
        WeaponServiceInterface, CrudServiceInterface<Weapon, Long, WeaponDTO, WeaponCreateDTO> {

    override fun getById(id: Long): WeaponDTO {
        val entity =
                weaponRepo.findById(id).orElseThrow {
                    NoSuchElementException("Weapon with id $id not found.")
                }
        return toDTO(entity)
    }

    override fun getAll(): List<WeaponDTO> {
        val entities = weaponRepo.findAll()
        return entities.map { toDTO(it) }
    }

    override fun create(dto: WeaponCreateDTO): WeaponDTO {
        val skin =
                skinRepo.findById(dto.skin).orElseThrow {
                    NoSuchElementException("Invalid skinId.")
                }

        if (skin.weapon != null) {
            throw IllegalStateException("This skin is already on a weapon.")
        }

        dto.skin = skin.id
        val entity = toEntity(dto)
        entity.skin = skin
        if (skinRepo.existsByNameAndPaintSeedAndFloatAndWeaponName(
                        skin.name,
                        skin.paintSeed,
                        skin.float,
                        entity.name
                )
        ) {
            throw IllegalArgumentException("Skin with this weapon already exists.")
        }

        val createdEntity = weaponRepo.save(entity)
        skin.weapon = createdEntity
        skinRepo.save(skin)

        val refreshedSkin =
                skinRepo.findById(skin.id).orElseThrow {
                    NoSuchElementException("Failed to refresh skin from db.")
                }
        createdEntity.skin = refreshedSkin

        return toDTO(createdEntity)
    }

    override fun deleteById(id: Long) {
        val weapon =
                weaponRepo.findById(id).orElseThrow {
                    NoSuchElementException("No weapon with id $id")
                }

        weapon.skin?.weapon = null
        weaponRepo.delete(weapon)
    }

    override fun updateWeaponTag(weaponId: Long, newTag: String): WeaponDTO {
        val existingWeapon =
                weaponRepo.findById(weaponId).orElseThrow {
                    NoSuchElementException("No weapon with id $weaponId")
                }

        existingWeapon.tag = newTag
        val savedEntity = weaponRepo.save(existingWeapon)
        return toDTO(savedEntity)
    }

    override fun toDTO(entity: Weapon): WeaponDTO {
        val skinDTO =
                entity.skin?.let {
                    SkinDTO(
                            id = it.id,
                            name = it.name,
                            rarity = it.rarity,
                            exterior = it.exterior,
                            price = it.price,
                            paintSeed = it.paintSeed,
                            float = it.float,
                            weapon = null
                    )
                }
                        ?: SkinDTO(
                                id = 0L,
                                name = "",
                                rarity = "",
                                exterior = "",
                                price = 0.0,
                                paintSeed = 0,
                                float = 0.0,
                                weapon = null
                        )
        return WeaponDTO(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                tag = entity.tag,
                skin = skinDTO
        )
    }

    override fun toEntity(dto: WeaponCreateDTO): Weapon {
        val skinR =
                skinRepo.findById(dto.skin).orElseThrow {
                    NoSuchElementException("Invalid skinId.")
                }

        return Weapon().apply {
            name = dto.name
            type = dto.type
            tag = dto.tag
            skin = skinR
        }
    }
}

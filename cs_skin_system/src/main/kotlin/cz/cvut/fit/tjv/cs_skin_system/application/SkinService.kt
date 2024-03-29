package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import cz.cvut.fit.tjv.cs_skin_system.exception.EntityHasDependencyException
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import kotlin.math.min
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
@Transactional
class SkinService(
        @Autowired var skinRepository: JPASkinRepository,
        @Autowired var caseRepo: JPACsgoCaseRepository,
        @Autowired var weaponService: WeaponService
) : SkinServiceInterface, CrudService<Skin, Long, SkinDTO, SkinCreateDTO>(skinRepository) {

    override fun updateSkinPrice(skinId: Long, newPrice: Double): SkinDTO {
        val skin =
                skinRepository.findById(skinId).orElseThrow {
                    EntityNotFoundException("No skin with id $skinId")
                }

        if (newPrice < 0) {
            throw IllegalArgumentException("Invalid newPrice $newPrice.")
        }
        skin.price = newPrice
        val savedEntity = skinRepository.save(skin)
        return toDTO(savedEntity)
    }

    override fun create(dto: SkinCreateDTO): SkinDTO {
        val entity = toEntity(dto)
        entity.paintSeed = min(entity.paintSeed, 1000)

        val savedEntity = skinRepository.save(entity)
        entity.dropsFrom.forEach { case ->
            case.contains.add(savedEntity)
            caseRepo.save(case)
        }

        return toDTO(savedEntity)
    }

    override fun getCasesForSkin(skinId: Long): List<CsgoCaseDTO> {
        val skin =
                skinRepository.findById(skinId).orElseThrow {
                    EntityNotFoundException("No skin with id $skinId")
                }
        return skin.dropsFrom.map { case -> CsgoCaseDTO(case.id, case.name, case.price) }
    }

    override fun updateSkinDropsFrom(skinId: Long, caseIds: List<Long>): SkinDTO {
        val skin =
                skinRepository.findById(skinId).orElseThrow {
                    EntityNotFoundException("No skin with id $skinId")
                }

        caseIds.forEach { caseId ->
            val csgoCase =
                    caseRepo.findById(caseId).orElseThrow {
                        EntityNotFoundException("No csgo case with id $caseId")
                    }
            skin.dropsFrom.add(csgoCase)
            csgoCase.contains.add(skin)
            caseRepo.save(csgoCase)
        }
        val savedEntity = skinRepository.save(skin)
        return toDTO(savedEntity)
    }

    override fun deleteById(id: Long) {
        val skin =
                skinRepository.findById(id).orElseThrow {
                    EntityNotFoundException("No skin with id $id")
                }

        if (skin.weapon != null) {
            throw EntityHasDependencyException(
                    "Skin id $id is associated with weapon, Remove the weapon first"
            )
        }

        skin.dropsFrom.forEach { csgoCase ->
            csgoCase.contains.remove(skin)
            caseRepo.save(csgoCase)
        }

        skinRepository.delete(skin)
    }

    override fun filterSkins(
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
    ): List<SkinDTO> {
        if (listOf(
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
                        .all { it == null }
        ) {
            return listOf()
        }
        val spec: Specification<Skin> =
                Specification.where { root: Root<Skin>, _: CriteriaQuery<*>, cb: CriteriaBuilder ->
                    skinId?.let { cb.equal(root.get<Long>("id"), it) }
                }
                        .and { root, _, cb -> name?.let { cb.equal(root.get<String>("name"), it) } }
                        .and { root, _, cb ->
                            rarity?.let { cb.equal(root.get<String>("rarity"), it) }
                        }
                        .and { root, _, cb ->
                            exterior?.let { cb.equal(root.get<String>("exterior"), it) }
                        }
                        .and { root, _, cb ->
                            price?.let { cb.greaterThanOrEqualTo(root.get("price"), it) }
                        }
                        .and { root, _, cb ->
                            paintSeed?.let { cb.equal(root.get<Int>("paintSeed"), it) }
                        }
                        .and { root, _, cb ->
                            float?.let { cb.lessThanOrEqualTo(root.get("float"), it) }
                        }
                        .and { root, _, cb ->
                            weaponId?.let {
                                cb.equal(root.get<Weapon>("weapon").get<Long>("id"), it)
                            }
                        }
                        .and { root, _, cb ->
                            weaponName?.let {
                                cb.equal(root.get<Weapon>("weapon").get<String>("name"), it)
                            }
                        }
                        .and { root, _, cb ->
                            csgoCaseId?.let {
                                val cases = root.join<Set<CsgoCase>, CsgoCase>("dropsFrom")
                                cb.equal(cases.get<Long>("id"), it)
                            }
                        }
                        .and { root, _, cb ->
                            csgoCaseName?.let {
                                val cases = root.join<Set<CsgoCase>, CsgoCase>("dropsFrom")
                                cb.equal(cases.get<String>("name"), it)
                            }
                        }
        val entities = skinRepository.findAll(spec)
        return entities.map { toDTO(it) }
    }

    override fun getSkinsWithNoWeapon(): List<SkinDTO> {
        val entities = skinRepository.findByWeaponIsNull()
        return entities.map { toDTO(it) }
    }

    override fun existsSkin(skinId: Long, weaponName: String): Boolean {
        val skin =
            skinRepository.findById(skinId).orElseThrow {
                EntityNotFoundException("No skin with id $skinId")
            }

        return skinRepository.existsByNameAndPaintSeedAndFloatAndWeaponName(
            skin.name,
            skin.paintSeed,
            skin.float,
            weaponName
        )
    }

    override fun toDTO(entity: Skin): SkinDTO {
        return SkinDTO(
                id = entity.id,
                name = entity.name,
                rarity = entity.rarity,
                exterior = entity.exterior,
                price = entity.price,
                paintSeed = entity.paintSeed,
                float = entity.float,
                weapon = entity.weapon?.let { weaponService.toDTO(it) }
        )
    }

    override fun toEntity(dto: SkinCreateDTO): Skin {
        val drops =
                dto.dropsFrom
                        ?.map { id ->
                            caseRepo.findById(id).orElseThrow {
                                EntityNotFoundException("No csgo case with id $id")
                            }
                        }
                        ?.toMutableSet()
                        ?: emptySet<CsgoCase>().toMutableSet()

        return Skin().apply {
            name = dto.name
            rarity = dto.rarity
            price = dto.price
            paintSeed = dto.paintSeed
            float = dto.float
            weapon = dto.weapon
            dropsFrom = drops
        }
    }
}

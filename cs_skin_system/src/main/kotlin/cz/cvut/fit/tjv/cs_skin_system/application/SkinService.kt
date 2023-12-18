package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.NoSuchElementException
import kotlin.math.min

@Service
@Transactional
class SkinService (@Autowired var skinRepository: JPASkinRepository,
                   @Autowired var caseRepo : JPACsgoCaseRepository,
                   @Autowired var weaponService: WeaponService
                  ) : SkinServiceInterface, CrudServiceInterface<Skin, Long, SkinDTO, SkinCreateDTO> {

    override fun getById(id: Long): SkinDTO {
        val entity = skinRepository.findById(id).orElseThrow {
            NoSuchElementException("No skin with id $id")
        }
        return toDTO(entity)
    }

    override fun getAll(): List<SkinDTO> {
        val entities = skinRepository.findAll()
        return entities.map { toDTO(it) }
    }

    override fun updateSkinPrice(skinId: Long, newPrice: Double): SkinDTO {
        val skin = skinRepository.findById(skinId)
            .orElseThrow { NoSuchElementException("No skin with id $skinId") }

        if (newPrice < 0 ){
            throw IllegalArgumentException("Invalid newPrice $newPrice.")
        }
        skin.price = newPrice
        val savedEntity = skinRepository.save(skin)
        return toDTO(savedEntity)
    }

    override fun create(dto: SkinCreateDTO, opt: Long?): SkinDTO {
        val entity = toEntity(dto)
        entity.exterior = when {
            entity.float <= 0.07 -> "Factory New"
            entity.float <= 0.15 -> "Minimal Wear"
            entity.float <= 0.37 -> "Field-Tested"
            entity.float <= 0.44 -> "Well-Worn"
            else -> {
                entity.float = min(entity.float, 1.0)
                "Battle-Scarred"
            }
        }
        entity.paintSeed = min(entity.paintSeed, 1000)

        val savedEntity = skinRepository.save(entity)
        for (case in entity.dropsFrom){
            case.contains.add(savedEntity)
            caseRepo.save(case)
        }

        return toDTO(savedEntity)
    }

    override fun updateSkinDropsFrom(skinId: Long, caseIds: List<Long>) : SkinDTO {
        val skin = skinRepository.findById(skinId)
            .orElseThrow { NoSuchElementException("No skin with id $skinId") }

        for (caseId in caseIds) {
            val csgoCase = caseRepo.findById(caseId)
                .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }
            skin.dropsFrom.add(csgoCase)
            csgoCase.contains.add(skin)
            caseRepo.save(csgoCase)
        }
        val savedEntity = skinRepository.save(skin)
        return toDTO(savedEntity)
    }

    override fun deleteById(id: Long){
        val skin = skinRepository.findById(id).
            orElseThrow { NoSuchElementException("No skin with id $id") }

        if (skin.weapon != null) {
            throw IllegalStateException("Skin id $id is associated with weapon, Remove the weapon first")
        }

        for (csgoCase in skin.dropsFrom) {
            csgoCase.contains.remove(skin)
            caseRepo.save(csgoCase)
        }

        skinRepository.delete(skin)
    }

    override fun filterSkins(skinId: Long?, name: String?, rarity: String?, exterior: String?,
                    price: Double?, paintSeed: Int?, float: Double?,
                    weaponId: Long?, weaponName: String?, csgoCaseId: Long?, csgoCaseName: String?): List<SkinDTO> {
        if (listOf(skinId, name, rarity, exterior, price, paintSeed,
                float, weaponId, weaponName, csgoCaseId, csgoCaseName).all { it == null }) {
            return listOf()
        }
        val spec: Specification<Skin> = Specification.where{
                root: Root<Skin>, _: CriteriaQuery<*>, cb: CriteriaBuilder ->
            skinId?.let { cb.equal(root.get<Long>("id"), it) }
        }.and { root, _, cb ->
            name?.let { cb.equal(root.get<String>("name"), it) }
        }.and { root, _, cb ->
            rarity?.let { cb.equal(root.get<String>("rarity"), it) }
        }.and { root, _, cb ->
            exterior?.let { cb.equal(root.get<String>("exterior"), it) }
        }.and { root, _, cb ->
            price?.let { cb.greaterThanOrEqualTo(root.get("price"), it) }
        }.and { root, _, cb ->
            paintSeed?.let { cb.equal(root.get<Int>("paintSeed"), it) }
        }.and { root, _, cb ->
            float?.let { cb.lessThanOrEqualTo(root.get("float"), it) }
        }.and { root, _, cb ->
            weaponId?.let { cb.equal(root.get<Weapon>("weapon").get<Long>("id"), it) }
        }.and { root, _, cb ->
            weaponName?.let { cb.equal(root.get<Weapon>("weapon").get<String>("name"), it) }
        }.and { root, _, cb ->
            csgoCaseId?.let {
                val cases = root.join<Set<CsgoCase>, CsgoCase>("dropsFrom")
                cb.equal(cases.get<Long>("id"), it)
            }
        }.and { root, _, cb ->
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
        val drops = dto.dropsFrom?.map { id ->
            caseRepo.findById(id).orElseThrow { NoSuchElementException("No csgo case with id $id") }
        }?.toMutableSet() ?: emptySet<CsgoCase>().toMutableSet()

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
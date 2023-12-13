package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
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
                  ) : SkinServiceInterface {

    override fun getSkinById(id: Long): Skin {
        return skinRepository.findById(id).orElseThrow {
                NoSuchElementException("No skin with id $id")
        }
    }

    override fun getSkins(): List<Skin> {
        return skinRepository.findAll()
    }

    override fun updateSkinPrice(skinId: Long, newPrice: Double): Skin {
        val skin = skinRepository.findById(skinId)
            .orElseThrow { NoSuchElementException("No skin with id $skinId") }

        if (newPrice < 0 ){
            throw IllegalArgumentException("Invalid newPrice $newPrice.")
        }
        skin.price = newPrice

        return skinRepository.save(skin)
    }

    override fun createSkin(skin: Skin, caseId: Long?): Skin {
        val weaponId = skin.weapon?.id
        if (weaponId != null && skinRepository
            .existsByNameAndPaintSeedAndFloatAndWeaponId(
                skin.name,
                skin.paintSeed,
                skin.float,
                weaponId)
            ) {
            throw IllegalArgumentException("Skin already exists.")
        }

        skin.exterior = when {
            skin.float <= 0.07 -> "Factory New"
            skin.float <= 0.15 -> "Minimal Wear"
            skin.float <= 0.37 -> "Field-Tested"
            skin.float <= 0.44 -> "Well-Worn"
            else -> {
                skin.float = min(skin.float, 1.0)
                "Battle-Scarred"
            }
        }
        skin.paintSeed = min(skin.paintSeed, 1000)

        if (caseId != null) {
            val csgoCase = caseRepo.findById(caseId)
                .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }
            skin.dropsFrom.add(csgoCase)
            csgoCase.contains.add(skin)
            caseRepo.save(csgoCase)
        }
        return skinRepository.save(skin)
    }

    override fun updateSkinDropsFrom(skinId: Long, caseIds: List<Long>) : Skin {
        val skin = skinRepository.findById(skinId)
            .orElseThrow { NoSuchElementException("No skin with id $skinId") }

        for (caseId in caseIds) {
            val csgoCase = caseRepo.findById(caseId)
                .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }
            skin.dropsFrom.add(csgoCase)
            csgoCase.contains.add(skin)
            caseRepo.save(csgoCase)
        }
    
        return skinRepository.save(skin)
    }

    override fun deleteSkin(skinId: Long){
        val skin = skinRepository.findById(skinId).
            orElseThrow { NoSuchElementException("No skin with id $skinId") }

        if (skin.weapon != null) {
            throw IllegalStateException("Skin id $skinId is associated with weapon, Remove the weapon first")
        }

        for (csgoCase in skin.dropsFrom) {
            csgoCase.contains.remove(skin)
            caseRepo.save(csgoCase)
        }

        skinRepository.delete(skin)
    }

    //TODO: remove getValuableSkins later ig
    override fun getValuableSkins(rarity: String, price: Double, caseName: String, weapon: String): List<Skin> {
        val skinsFromRepo = skinRepository.findByRarityAndPriceAndCsgoCase(rarity, price, caseName)
        val valuableSkins = skinsFromRepo.filter { it.weapon?.name == weapon}

        return valuableSkins
    }
    override fun filterSkins(skinId: Long?, name: String?, rarity: String?, exterior: String?,
                    price: Double?, paintSeed: Int?, float: Double?,
                    weaponId: Long?, weaponName: String?, csgoCaseId: Long?, csgoCaseName: String?): List<Skin> {
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

        return skinRepository.findAll(spec)
    }

    override fun getSkinsWithNoWeapon(): List<Skin> {
        return skinRepository.findByWeaponIsNull()
    }
}
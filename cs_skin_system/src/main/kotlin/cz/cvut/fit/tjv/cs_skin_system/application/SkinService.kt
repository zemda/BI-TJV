package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.NoSuchElementException

@Service
@Transactional
class SkinService ( @Autowired var skinRepository: JPASkinRepository,
                    @Autowired var csgoCaseService: CsgoCaseService
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
        if (skinRepository.existsByNameAndPaintSeedAndFloatAndWeaponId(skin.name, skin.paintSeed, skin.float, skin.weapon?.id!!)) {
            throw IllegalArgumentException("Skin already exists.")
        }
        val createdSkin = skinRepository.save(skin)
        if (caseId != null) {
            csgoCaseService.updateCsgoCase(caseId, skin.id, true)
        }

        return createdSkin
    }

    override fun deleteSkin(skin: Skin){
        if (skinRepository.existsById(skin.id)) {
            skinRepository.delete(skin)
        } else {
            throw NoSuchElementException("No skin with id ${skin.id}")
        }
    }

    override fun getValuableSkins(rarity: String, price: Double, caseName: String, weapon: String): List<Skin> {
        val skinsFromRepo = skinRepository.findByRarityAndPriceAndCsgoCase(rarity, price, caseName)
        val valuableSkins = skinsFromRepo.filter { it.weapon?.name == weapon}

        return valuableSkins
    }

}
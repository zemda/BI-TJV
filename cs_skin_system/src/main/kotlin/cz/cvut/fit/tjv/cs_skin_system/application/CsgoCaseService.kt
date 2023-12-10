package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Transactional
class CsgoCaseService (@Autowired var caseRepo : JPACsgoCaseRepository,
                       @Autowired var skinRepo : JPASkinRepository
                      ) : CsgoCaseServiceInterface {


    override fun getCsgoCaseById(id: Long): CsgoCase {
        return caseRepo.findById(id).orElseThrow {
            NoSuchElementException("No case with id $id")
        }
    }

    override fun getCsgoCases(): List<CsgoCase> {
        return caseRepo.findAll()
    }

    override fun createCsgoCase(csgoCase: CsgoCase): CsgoCase {
        if (caseRepo.existsByName(csgoCase.name)) {
            throw IllegalArgumentException("Case with name ${csgoCase.name} already exists.")
        }

        return caseRepo.save(csgoCase)
    }

    override fun updateCsgoCase(caseId: Long, skinId: Long, addSkin: Boolean): CsgoCase {
        val case = caseRepo.findById(caseId)
            .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }

        val skin = skinRepo.findById(skinId)
            .orElseThrow { NoSuchElementException("No skin with id $skinId") }

        if (addSkin) {
            val isAdded = case.contains.add(skin)
            if (!isAdded) {
                throw IllegalStateException("Skin with id $skinId is already in the case with id $caseId")
            }
        } else {
            val wasRemoved = case.contains.remove(skin)
            if (!wasRemoved) {
                throw IllegalStateException("No skin with id $skinId found in the case with id $caseId")
            }
        }

        return caseRepo.save(case)
    }

    override fun updateCsgoCase(caseId: Long, newPrice: Double): CsgoCase {
        val case = caseRepo.findById(caseId)
            .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }

        if (newPrice < 0 ){
            throw IllegalArgumentException("Invalid newPrice $newPrice.")
        }
        case.price = newPrice

        return caseRepo.save(case)
    }

    override fun updateCsgoCase(caseId: Long, skinIds: List<Long>, addSkins: Boolean): CsgoCase {
        val csgoCase = caseRepo.findById(caseId)
            .orElseThrow { NoSuchElementException("No csgo case with id $caseId") }

        skinIds.forEach { skinId ->
            val skin = skinRepo.findById(skinId)
                .orElseThrow { NoSuchElementException("No skin with id $skinId") }

            if (addSkins) {
                if (!csgoCase.contains.add(skin)) {
                    throw IllegalStateException("Skin with id $skinId is already in the case with id $caseId")
                }
            } else {
                if (!csgoCase.contains.remove(skin)) {
                    throw IllegalStateException("No skin with id $skinId found in the case with id $caseId")
                }
            }
        }

        return caseRepo.save(csgoCase)
    }

    override fun deleteCsgoCase(csgoCase: CsgoCase) {
        if (caseRepo.existsById(csgoCase.id)) {
            caseRepo.delete(csgoCase)
        } else {
            throw NoSuchElementException("No case with id ${csgoCase.id}")
        }
    }
}
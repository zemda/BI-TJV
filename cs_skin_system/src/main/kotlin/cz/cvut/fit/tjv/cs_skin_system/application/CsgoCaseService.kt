package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO
import cz.cvut.fit.tjv.cs_skin_system.exception.EntityRelationshipExistsException
import cz.cvut.fit.tjv.cs_skin_system.exception.EntityRelationshipNotFoundException
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Transactional
class CsgoCaseService(
        @Autowired var caseRepo: JPACsgoCaseRepository,
        @Autowired var skinRepo: JPASkinRepository
) : CsgoCaseServiceInterface, CrudService<CsgoCase, Long, CsgoCaseDTO, CsgoCaseCreateDTO>(caseRepo) {

    override fun create(dto: CsgoCaseCreateDTO): CsgoCaseDTO {
        if (dto.contains.isNullOrEmpty()) {
            throw IllegalArgumentException("Case must contain at least one skin.")
        }
        val entity = toEntity(dto)
        if (caseRepo.existsByName(entity.name)) {
            throw EntityExistsException("Case with name ${entity.name} already exists.")
        }

        val savedEntity = caseRepo.save(entity)
        entity.contains.forEach { skin ->
            skin.dropsFrom.add(savedEntity)
            skinRepo.save(skin)
        }

        return toDTO(savedEntity)
    }

    override fun deleteById(id: Long) {
        val case =
                caseRepo.findById(id).orElseThrow { EntityNotFoundException("No case with id $id") }

        case.contains.forEach { skin ->
            skin.dropsFrom.remove(case)
            skinRepo.save(skin)
        }

        caseRepo.delete(case)
    }

    override fun updateCsgoCase(caseId: Long, newPrice: Double): CsgoCaseDTO {
        val case =
                caseRepo.findById(caseId).orElseThrow {
                    EntityNotFoundException("No csgo case with id $caseId")
                }

        if (newPrice < 0) {
            throw IllegalArgumentException("Invalid newPrice $newPrice.")
        }
        case.price = newPrice
        val savedEntity = caseRepo.save(case)
        return toDTO(savedEntity)
    }

    override fun updateCsgoCase(caseId: Long, skinIds: List<Long>, addSkins: Boolean): CsgoCaseDTO {
        val csgoCase =
                caseRepo.findById(caseId).orElseThrow {
                    EntityNotFoundException("No csgo case with id $caseId")
                }

        skinIds.forEach { skinId ->
            val skin =
                    skinRepo.findById(skinId).orElseThrow {
                        EntityNotFoundException("No skin with id $skinId")
                    }

            if (addSkins) {
                if (!csgoCase.contains.add(skin) || !skin.dropsFrom.add(csgoCase)) {
                    throw EntityRelationshipExistsException(
                            "Skin with id $skinId is already in the case with id $caseId"
                    )
                }
            } else {
                if (!csgoCase.contains.remove(skin) || !skin.dropsFrom.remove(csgoCase)) {
                    throw EntityRelationshipNotFoundException(
                            "No skin with id $skinId found in the case with id $caseId"
                    )
                }
            }
            skinRepo.save(skin)
        }
        val savedEntity = caseRepo.save(csgoCase)
        return toDTO(savedEntity)
    }

    override fun toDTO(entity: CsgoCase): CsgoCaseDTO {
        return CsgoCaseDTO(id = entity.id, name = entity.name, price = entity.price)
    }

    override fun toEntity(dto: CsgoCaseCreateDTO): CsgoCase {
        val skins =
                dto.contains
                        ?.map { id ->
                            skinRepo.findById(id).orElseThrow {
                                EntityNotFoundException("No skin with id $id")
                            }
                        }
                        ?.toMutableSet()
                        ?: emptySet<Skin?>().toMutableSet()

        return CsgoCase().apply {
            name = dto.name
            price = dto.price
            contains = skins
        }
    }
}

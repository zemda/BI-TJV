package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.dto.CsgoCaseDTO

interface CsgoCaseServiceInterface {
    fun updateCsgoCase(caseId: Long, newPrice: Double): CsgoCaseDTO
    fun updateCsgoCase(caseId: Long, skinIds: List<Long>, addSkins: Boolean): CsgoCaseDTO
}

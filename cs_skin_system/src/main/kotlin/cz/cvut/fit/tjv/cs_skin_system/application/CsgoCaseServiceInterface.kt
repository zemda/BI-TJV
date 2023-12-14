package cz.cvut.fit.tjv.cs_skin_system.application

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin

interface CsgoCaseServiceInterface {

    fun updateCsgoCase(caseId: Long, newPrice: Double): CsgoCase

    fun updateCsgoCase(caseId: Long, skinIds: List<Long>, addSkins: Boolean) : CsgoCase

}
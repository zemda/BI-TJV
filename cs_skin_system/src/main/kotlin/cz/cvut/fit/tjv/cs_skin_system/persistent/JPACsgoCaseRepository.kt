package cz.cvut.fit.tjv.cs_skin_system.persistent

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JPACsgoCaseRepository : JpaRepository<CsgoCase, Long>{
    fun existsByName(name: String): Boolean
}
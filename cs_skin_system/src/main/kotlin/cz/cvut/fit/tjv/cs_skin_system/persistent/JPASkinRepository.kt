package cz.cvut.fit.tjv.cs_skin_system.persistent

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JPASkinRepository : JpaRepository<Skin, Long>, JpaSpecificationExecutor<Skin> {
    fun existsByNameAndPaintSeedAndFloatAndWeaponName(
            name: String,
            paintSeed: Int,
            float: Double,
            weaponName: String
    ): Boolean

    fun findByWeaponIsNull(): List<Skin>
}

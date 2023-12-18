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

    @Query(
            "SELECT s FROM Skin s JOIN s.dropsFrom c WHERE s.rarity = :rarity AND s.price > :price AND c.name = :caseName"
    )
    fun findByRarityAndPriceAndCsgoCase(
            @Param("rarity") rarity: String,
            @Param("price") price: Double,
            @Param("caseName") caseName: String
    ): List<Skin>

    fun findByWeaponIsNull(): List<Skin>
}

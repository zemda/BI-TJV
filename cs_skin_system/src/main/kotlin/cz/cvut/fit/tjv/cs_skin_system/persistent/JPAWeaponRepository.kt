package cz.cvut.fit.tjv.cs_skin_system.persistent

import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JPAWeaponRepository : JpaRepository<Weapon, Long>
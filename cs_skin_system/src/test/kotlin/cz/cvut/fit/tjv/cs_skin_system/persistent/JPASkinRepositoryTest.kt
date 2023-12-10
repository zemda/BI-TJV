package cz.cvut.fit.tjv.cs_skin_system.persistent

import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class JPASkinRepositoryTest @Autowired constructor(
    private val entityManager: TestEntityManager,
    private val skinRepository: JPASkinRepository
) {
    @Test
    fun `Skin query test`() {

        val myWeapon = Weapon().apply {
            name = "AK-47"
            type = "rifle"
            tag = "My AK-47"
        }

        val mySkin = Skin().apply {
            name = "Fire Beast"
            rarity = "covert"
            quality = "Extraordinary"
            price = 250.0
            paintSeed = 123
            float = .255
        }

        val myCase = CsgoCase().apply {
            name = "Danger Zone"
            price = 50.0
        }

        mySkin.weapon = myWeapon
        mySkin.dropsFrom.add(myCase)
        myWeapon.skin = mySkin
        myCase.contains.add(mySkin)

        entityManager.persist(myCase)
        entityManager.persist(myWeapon)
        entityManager.persist(mySkin)
        entityManager.flush()

        val skinsFromRepo = skinRepository.findByRarityAndPriceAndCsgoCase(mySkin.rarity, mySkin.price - 0.01, myCase.name)
        Assertions.assertEquals(1, skinsFromRepo.size)
        val skinFromRepo = skinsFromRepo[0]

        assertThat(skinFromRepo.name).isEqualTo(mySkin.name)
        assertThat(skinFromRepo.rarity).isEqualTo(mySkin.rarity)
        assertThat(skinFromRepo.quality).isEqualTo(mySkin.quality)
        assertThat(skinFromRepo.price).isEqualTo(mySkin.price)
        assertThat(skinFromRepo.paintSeed).isEqualTo(mySkin.paintSeed)
        assertThat(skinFromRepo.float).isEqualTo(mySkin.float)

        assertThat(skinFromRepo.weapon).isEqualTo(mySkin.weapon)
        assertThat(skinFromRepo.dropsFrom).isEqualTo(mySkin.dropsFrom)

    }
}
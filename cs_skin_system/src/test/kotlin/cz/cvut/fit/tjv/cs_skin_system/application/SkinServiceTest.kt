import cz.cvut.fit.tjv.cs_skin_system.application.CsgoCaseService
import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class SkinServiceTest {

    @InjectMocks
    lateinit var skinService: SkinService

    @Mock
    lateinit var skinRepo: JPASkinRepository

    @Mock
    lateinit var csgoCaseService: CsgoCaseService


    @Test
    fun `updateSkin valid price test`() {
        val existingSkin = Skin().apply { id = 1L; price = 100.0 }
        val newPrice = 200.0

        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(existingSkin))
        `when`(skinRepo.save(any(Skin::class.java))).thenReturn(Skin().apply { id = 1L; price = newPrice })

        val updatedSkin = skinService.updateSkinPrice(existingSkin.id, newPrice)

        verify(skinRepo, times(1)).findById(1L)
        verify(skinRepo, times(1)).save(any(Skin::class.java))
        assert(updatedSkin.price == newPrice)

        reset(skinRepo)
    }

    @Test
    fun `updateSkinPrice invalid price test`() {
        val skin = Skin().apply { id = 1L; price = 10.0; }
        val invalidPrice = -5.0

        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(skin))

        val exception = assertThrows<IllegalArgumentException> {
            skinService.updateSkinPrice(skin.id, invalidPrice)
        }

        Assertions.assertEquals(
            "Invalid newPrice $invalidPrice.", exception.message
        )

        verify(skinRepo, times(1)).findById(1L)
        verify(skinRepo, never()).save(any(Skin::class.java))

        reset(skinRepo)
    }

    @Test
    fun `getValuableSkins returns only valuable skins`() {
        val expectedCase = CsgoCase().apply { name = "case1" }
        val case2 = CsgoCase().apply { name = "case2" }
        val expectedSkin = Skin().apply {
            id = 1L
            name = "Asiimov"
            rarity = "rare"
            price = 100.0
            dropsFrom = mutableSetOf(expectedCase, case2)
            weapon = Weapon().apply { name = "weapon1" }
        }

        val nonValuableSkin = Skin().apply {
            id = 2L
            rarity = "common"
            price = 59.0
            dropsFrom = mutableSetOf(expectedCase, case2)
            weapon = Weapon().apply { name = "weapon2" }
        }

        `when`(skinRepo.findByRarityAndPriceAndCsgoCase(anyString(), anyDouble(), anyString())
        ).thenReturn(listOf(expectedSkin, nonValuableSkin))

        val valuableSkins = skinService.getValuableSkins("rare", 100.0, "case1", "weapon1")

        assertEquals(1, valuableSkins.size)
        assertEquals("weapon1", valuableSkins[0].weapon?.name)
        assertEquals("Asiimov", valuableSkins[0].name)

        verify(skinRepo, times(1)).findByRarityAndPriceAndCsgoCase(anyString(), anyDouble(), anyString())

        reset(skinRepo)
    }
}
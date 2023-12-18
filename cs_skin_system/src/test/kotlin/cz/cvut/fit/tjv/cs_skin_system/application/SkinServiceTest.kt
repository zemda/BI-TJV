import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SkinServiceTest {

    @InjectMocks lateinit var skinService: SkinService

    @Mock lateinit var skinRepo: JPASkinRepository

    @Mock lateinit var csgoCaseRepo: JPACsgoCaseRepository

    @Mock lateinit var weaponService: WeaponService

    @Test
    fun `updateSkin valid price test`() {
        val existingSkin =
                Skin().apply {
                    id = 1L
                    price = 100.0
                }
        val newPrice = 200.0

        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(existingSkin))
        `when`(skinRepo.save(any(Skin::class.java)))
                .thenReturn(
                        Skin().apply {
                            id = 1L
                            price = newPrice
                        }
                )

        val updatedSkin = skinService.updateSkinPrice(existingSkin.id, newPrice)

        verify(skinRepo, times(1)).findById(1L)
        verify(skinRepo, times(1)).save(any(Skin::class.java))
        assert(updatedSkin.price == newPrice)

        reset(skinRepo)
    }

    @Test
    fun `updateSkinPrice invalid price test`() {
        val skin =
                Skin().apply {
                    id = 1L
                    price = 10.0
                }
        val invalidPrice = -5.0

        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(skin))

        val exception =
                assertThrows<IllegalArgumentException> {
                    skinService.updateSkinPrice(skin.id, invalidPrice)
                }

        assertEquals("Invalid newPrice $invalidPrice.", exception.message)

        verify(skinRepo, times(1)).findById(1L)
        verify(skinRepo, never()).save(any(Skin::class.java))

        reset(skinRepo)
    }
}

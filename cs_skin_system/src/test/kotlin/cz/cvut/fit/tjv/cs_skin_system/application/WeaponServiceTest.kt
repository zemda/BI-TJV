import cz.cvut.fit.tjv.cs_skin_system.persistent.JPAWeaponRepository
import cz.cvut.fit.tjv.cs_skin_system.domain.Weapon
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
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
class WeaponServiceTest {

    @InjectMocks
    lateinit var weaponService: WeaponService

    @Mock
    lateinit var weaponRepo: JPAWeaponRepository

    @Mock
    lateinit var skinRepo: JPASkinRepository

    @Test
    fun `updateWeaponTag test`() {
        val existingWeapon = Weapon().apply { id = 1L; tag = "Old Tag" }
        val newTag = "New Tag"

        `when`(weaponRepo.findById(1L)).thenReturn(Optional.of(existingWeapon))
        `when`(weaponRepo.save(any(Weapon::class.java))).thenReturn(Weapon().apply { id = 1L; tag = newTag })


        val updatedWeapon = weaponService.updateWeaponTag(existingWeapon.id, newTag)

        verify(weaponRepo, times(1)).findById(1L)
        verify(weaponRepo, times(1)).save(any(Weapon::class.java))
        assert(updatedWeapon.tag == "New Tag")

        reset(weaponRepo)
    }

    @Test
    fun `createWeapon assigns skin successfully`() {
        val newSkin = Skin().apply { id = 1L; weapon = null }
        val newSkinDTO = SkinDTO(id = newSkin.id, name = newSkin.name,
            rarity = newSkin.rarity, exterior = newSkin.exterior, price = newSkin.price,
            paintSeed = newSkin.paintSeed, float = newSkin.float, weapon = null
        )
        val weapon = Weapon().apply { id = 2L; skin = null }
        val weaponCreateDTO = WeaponCreateDTO(name = "Test Weapon", type = "Rifle", tag = "Test Tag", skin = 1L)

        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(newSkin))
        `when`(weaponRepo.save(any())).thenReturn(weapon)
        `when`(skinRepo.save(any())).thenReturn(newSkin)

        val createdWeapon = weaponService.create(weaponCreateDTO, 1L)

        verify(weaponRepo, times(1)).save(any())
        verify(skinRepo, times(1)).save(newSkin)

        assertEquals(newSkinDTO, createdWeapon.skin)
    }

    @Test
    fun `createWeapon throws exception when skin is already assigned to another weapon`() {
        val otherWeapon = Weapon().apply { id = 3L; }
        val assignedSkin = Skin().apply { id = 1L; weapon = otherWeapon }
        val weaponCreateDTO = WeaponCreateDTO(name = "Test Weapon", type = "Rifle", tag = "Test Tag", skin = 1L)
        `when`(skinRepo.findById(1L)).thenReturn(Optional.of(assignedSkin))

        assertThrows<Exception> {
            weaponService.create(weaponCreateDTO, 1L)
        }

        verify(weaponRepo, never()).save(any())
    }
}
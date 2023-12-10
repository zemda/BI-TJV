import cz.cvut.fit.tjv.cs_skin_system.application.CsgoCaseService
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPACsgoCaseRepository
import cz.cvut.fit.tjv.cs_skin_system.domain.CsgoCase
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class CsgoCaseServiceTest {

    @InjectMocks
    lateinit var csgoCaseService: CsgoCaseService

    @Mock
    lateinit var caseRepo: JPACsgoCaseRepository

    @Mock
    lateinit var skinRepo: JPASkinRepository

    @Test
    fun `updateCsgoCase add already existent skin to case test`() {
        val existingCase = CsgoCase().apply { id = 1L }
        val skin = Skin().apply { id = 100L }
        existingCase.contains.add(skin)

        `when`(caseRepo.findById(1L)).thenReturn(Optional.of(existingCase))
        `when`(skinRepo.findById(100L)).thenReturn(Optional.of(skin))

        val exception = assertThrows<IllegalStateException> {
            csgoCaseService.updateCsgoCase(existingCase.id, skin.id, true)
        }

        assertEquals("Skin with id ${skin.id} is already in the case with id ${existingCase.id}", exception.message)

        verify(caseRepo, times(1)).findById(1L)
        verify(skinRepo, times(1)).findById(100L)
    }

    @Test
    fun `updateCsgoCase remove skin from case which is not there test`() {
        val existingCase = CsgoCase().apply { id = 1L }
        val skin = Skin().apply { id = 100L }

        `when`(caseRepo.findById(1L)).thenAnswer { Optional.of(existingCase) }
        `when`(skinRepo.findById(100L)).thenReturn(Optional.of(skin))

        val exception = assertThrows<IllegalStateException> {
            csgoCaseService.updateCsgoCase(existingCase.id, skin.id, false)
        }

        assertEquals("No skin with id ${skin.id} found in the case with id ${existingCase.id}", exception.message)

        verify(caseRepo, times(1)).findById(1L)
        verify(caseRepo, never()).save(any(CsgoCase::class.java))
    }

    @Test
    fun `updateCsgoCase add skin test`() {
        val existingCase = CsgoCase().apply { id = 1L; }
        val skin = Skin().apply { id = 100L; }

        `when`(caseRepo.findById(1L)).thenReturn(Optional.of(existingCase))
        `when`(caseRepo.save(any(CsgoCase::class.java))).thenReturn(existingCase)
        `when`(skinRepo.findById(100L)).thenReturn(Optional.of(skin))

        val updatedCase = csgoCaseService.updateCsgoCase(existingCase.id, skin.id, true)

        verify(caseRepo, times(1)).findById(1L)
        verify(caseRepo, times(1)).save(any(CsgoCase::class.java))
        assert(updatedCase.contains.contains(skin))

        reset(caseRepo)
    }

    @Test
    fun `updateCsgoCase test for remove skin`() {
        val existingCase = CsgoCase().apply { id = 1L; }
        val skin = Skin().apply { id = 100L; }
        existingCase.contains.add(skin)

        `when`(caseRepo.findById(1L)).thenReturn(Optional.of(existingCase))
        `when`(caseRepo.save(any(CsgoCase::class.java))).thenReturn(existingCase)
        `when`(skinRepo.findById(100L)).thenReturn(Optional.of(skin))

        val updatedCase = csgoCaseService.updateCsgoCase(existingCase.id, skin.id, false)

        verify(caseRepo, times(1)).findById(1L)
        verify(caseRepo, times(1)).save(any(CsgoCase::class.java))
        assert(!updatedCase.contains.contains(skin))

        reset(caseRepo)
    }

    @Test
    fun `updateCsgoCase test for update case price`() {
        val existingCase = CsgoCase().apply { id = 1L; price = 100.0 }
        val newPrice = 200.0

        `when`(caseRepo.findById(1L)).thenReturn(Optional.of(existingCase))
        `when`(caseRepo.save(any(CsgoCase::class.java))).thenReturn(CsgoCase().apply { id = 1L; price = newPrice })

        val updatedCase = csgoCaseService.updateCsgoCase(existingCase.id, newPrice)

        verify(caseRepo, times(1)).findById(1L)
        verify(caseRepo, times(1)).save(any(CsgoCase::class.java))
        assert(updatedCase.price == newPrice)

        reset(caseRepo)
    }

    @Test
    fun `updateCsgoCase test for invalid new price`() {
        val existingCase = CsgoCase().apply { id = 1L; price = 100.0 }
        val newPrice = -200.0  // Invalid cuz < 0

        `when`(caseRepo.findById(1L)).thenReturn(Optional.of(existingCase))

        assertThrows(IllegalArgumentException::class.java) {
            csgoCaseService.updateCsgoCase(existingCase.id, newPrice)
        }

        verify(caseRepo, never()).save(any(CsgoCase::class.java))

        reset(caseRepo)
    }
}
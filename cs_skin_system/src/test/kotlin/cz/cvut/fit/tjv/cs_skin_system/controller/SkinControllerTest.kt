package cz.cvut.fit.tjv.cs_skin_system.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.beans.factory.annotation.Autowired
import com.fasterxml.jackson.databind.ObjectMapper
import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`

@ExtendWith(SpringExtension::class)
@WebMvcTest(SkinController::class)
class SkinControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var skinService: SkinService

    @Test
    fun `createSkin create new skin and return status CREATED`() {
        val skin = Skin().apply {
            name = "Test Skin"
            rarity = "Rare"
            float = 0.1
            paintSeed = 10
        }

        mockMvc.perform(post("/skins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(skin)))
            .andExpect(status().isCreated)
    }

    @Test
    fun `getSkinById return skin if it exists`() {
        val id = 1L
        val skinDto = SkinDTO(id, "Test Skin", "Rare", "idk", 0.001,104, 0.001, null)

        `when`(skinService.getById(id)).thenReturn(skinDto)

        mockMvc.perform(get("/skins/$id")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteSkin delete a skin if it exists`() {
        val id = 1L

        doNothing().`when`(skinService).deleteById(id)

        mockMvc.perform(delete("/skins/$id")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)
    }
}
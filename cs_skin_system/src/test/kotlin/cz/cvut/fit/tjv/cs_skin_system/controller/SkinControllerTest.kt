package cz.cvut.fit.tjv.cs_skin_system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import cz.cvut.fit.tjv.cs_skin_system.application.SkinService
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SkinController::class)
class SkinControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockBean lateinit var skinService: SkinService

    @Test
    fun whenCreateSkinCalled_thenReturnStatusCreated() {
        val skin = SkinCreateDTO("Test Skin", "Rare", 0.1, 10, 0.002, null, null)
        val skinDto = SkinDTO(1L, "Test Skin", "Rare", "Factory New", 0.1, 10, 0.002, null)

        `when`(skinService.create(skin)).thenReturn(skinDto)
        mockMvc.perform(
                        post("/skins")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(skin))
                )
                .andExpect(status().isCreated)
                .andExpect(content().json(objectMapper.writeValueAsString(skinDto)))
        verify(skinService, times(1)).create(skin)
    }

    @Test
    fun whenGetSkinByIdCalled_thenReturnSkinIfExists() {
        val id = 1L
        val skinDto = SkinDTO(id, "Test Skin", "Rare", "idk", 0.001, 104, 0.001, null)

        `when`(skinService.getById(id)).thenReturn(skinDto)

        mockMvc.perform(get("/skins/$id").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().json(objectMapper.writeValueAsString(skinDto)))
        verify(skinService, times(1)).getById(id)
    }

    @Test
    fun whenDeleteSkinCalled_thenReturnStatusNoContent() {
        val id = 1L

        doNothing().`when`(skinService).deleteById(id)

        mockMvc.perform(delete("/skins/$id").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)

        verify(skinService, times(1)).deleteById(id)
    }
}

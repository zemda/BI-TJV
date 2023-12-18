package cz.cvut.fit.tjv.cs_skin_system.controller
//
//import cz.cvut.fit.tjv.cs_skin_system.application.WeaponService
//import cz.cvut.fit.tjv.cs_skin_system.dto.SkinDTO
//import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
//import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.Mockito
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpMethod
//import org.springframework.http.HttpStatus
//import org.springframework.test.context.junit.jupiter.SpringExtension
//
//@ExtendWith(SpringExtension::class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class WeaponControllerTest(@LocalServerPort val port: Int) {
//
//    @Autowired lateinit var restTemplate: TestRestTemplate
//
//    @MockBean lateinit var weaponService: WeaponService
//
//    @Test
//    fun `createWeapon, updateWeaponTag and deleteWeapon test`() {
//        val savedSkinDTO =
//                SkinDTO(
//                        id = 5L,
//                        name = "test name",
//                        rarity = "test rarity",
//                        exterior = "idk",
//                        price = 68.0,
//                        paintSeed = 123,
//                        float = 0.001,
//                        weapon = null
//                )
//
//        val weaponCreateDTO =
//                WeaponCreateDTO(
//                        name = "Test Weapon",
//                        type = "Rifle",
//                        tag = "Test Tag",
//                        skin = savedSkinDTO.id
//                )
//        val createdWeaponDTO =
//                WeaponDTO(
//                        id = 1L,
//                        name = weaponCreateDTO.name,
//                        type = weaponCreateDTO.type,
//                        tag = weaponCreateDTO.tag,
//                        skin = savedSkinDTO
//                )
//        Mockito.`when`(weaponService.create(weaponCreateDTO)).thenReturn(createdWeaponDTO)
//
//        val createResponse =
//                restTemplate.postForEntity(
//                        "http://localhost:$port/weapons?skinId=5",
//                        weaponCreateDTO,
//                        WeaponDTO::class.java
//                )
//        assertEquals(HttpStatus.CREATED, createResponse.statusCode)
//        assertEquals(savedSkinDTO.id, createResponse.body?.skin?.id)
//        assertEquals(savedSkinDTO.name, createResponse.body?.skin?.name)
//        assertEquals(createdWeaponDTO.name, createResponse.body?.name)
//        assertEquals(createdWeaponDTO.tag, createResponse.body?.tag)
//
//        val newTag = "New Test Tag"
//        val updatedWeaponDTO =
//                WeaponDTO(
//                        id = createdWeaponDTO.id,
//                        name = createdWeaponDTO.name,
//                        type = createdWeaponDTO.type,
//                        tag = newTag,
//                        skin = savedSkinDTO
//                )
//        Mockito.`when`(weaponService.updateWeaponTag(createdWeaponDTO.id, newTag))
//                .thenReturn(updatedWeaponDTO)
//
//        val updateResponse =
//                restTemplate.exchange(
//                        "http://localhost:$port/weapons/${createdWeaponDTO.id}/tag?newTag=$newTag",
//                        HttpMethod.PUT,
//                        HttpEntity<Any>(HttpHeaders()),
//                        WeaponDTO::class.java
//                )
//
//        assertEquals(HttpStatus.OK, updateResponse.statusCode)
//        assertEquals(newTag, updateResponse.body?.tag)
//
//        val deleteResponse =
//                restTemplate.exchange(
//                        "http://localhost:$port/weapons/${createdWeaponDTO.id}",
//                        HttpMethod.DELETE,
//                        HttpEntity<Any>(HttpHeaders()),
//                        Void::class.java
//                )
//        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.statusCode)
//    }
//}

package cz.cvut.fit.tjv.cs_skin_system.controller

import cz.cvut.fit.tjv.cs_skin_system.domain.Skin
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponCreateDTO
import cz.cvut.fit.tjv.cs_skin_system.dto.WeaponDTO
import cz.cvut.fit.tjv.cs_skin_system.persistent.JPASkinRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeaponControllerTest(@LocalServerPort val port: Int) {

    @Autowired lateinit var restTemplate: TestRestTemplate

    @Autowired lateinit var skinRepository: JPASkinRepository

    @Test
    fun `createWeapon, updateWeaponTag and deleteWeapon test`() {

        val savedSkin =
                skinRepository.save(
                        Skin().apply {
                            id = 5L
                            name = "test name"
                            rarity = "test rarity"
                            exterior = "idk"
                            price = 68.0
                            paintSeed = 123
                            float = 0.001
                            weapon = null
                        }
                )

        val weaponCreateDTO =
                WeaponCreateDTO(
                        name = "Test Weapon",
                        type = "Rifle",
                        tag = "Test Tag",
                        skin = savedSkin.id
                )

        val createResponse =
                restTemplate.postForEntity(
                        "http://localhost:$port/weapons?skinId=${savedSkin.id}",
                        weaponCreateDTO,
                        WeaponDTO::class.java
                )
        assertEquals(HttpStatus.CREATED, createResponse.statusCode)
        assertEquals(savedSkin.id, createResponse.body?.skin?.id)
        assertEquals(savedSkin.name, createResponse.body?.skin?.name)
        assertEquals(weaponCreateDTO.name, createResponse.body?.name)
        assertEquals(weaponCreateDTO.tag, createResponse.body?.tag)

        val newTag = "New Test Tag"
        val updateResponse =
                restTemplate.exchange(
                        "http://localhost:$port/weapons/${createResponse.body?.id}/tag?newTag=$newTag",
                        HttpMethod.PUT,
                        HttpEntity<Any>(HttpHeaders()),
                        WeaponDTO::class.java
                )
        assertEquals(HttpStatus.OK, updateResponse.statusCode)
        assertEquals(newTag, updateResponse.body?.tag)

        val deleteResponse =
                restTemplate.exchange(
                        "http://localhost:$port/weapons/${createResponse.body?.id}",
                        HttpMethod.DELETE,
                        HttpEntity<Any>(HttpHeaders()),
                        Void::class.java
                )
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.statusCode)

        val getResponseAfterDelete =
                restTemplate.exchange(
                        "http://localhost:$port/weapons/${createResponse.body?.id}",
                        HttpMethod.GET,
                        null,
                        String::class.java
                )
        assertEquals(HttpStatus.NOT_FOUND, getResponseAfterDelete.statusCode)
    }
}

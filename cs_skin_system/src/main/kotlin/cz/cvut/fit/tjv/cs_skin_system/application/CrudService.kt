package cz.cvut.fit.tjv.cs_skin_system.application

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

abstract class CrudService<Entity, ID : Any, DTO, CreateDTO>(
        private val repository: JpaRepository<Entity, ID>,
) : CrudServiceInterface<Entity, ID, DTO, CreateDTO> {

    override fun getById(id: ID): DTO {
        val entity =
                repository.findById(id).orElseThrow {
                    EntityNotFoundException("Entity with id $id not found")
                }
        return toDTO(entity)
    }

    override fun getAll(): List<DTO> {
        return repository.findAll().map(this::toDTO)
    }

    abstract override fun create(dto: CreateDTO): DTO
    abstract override fun deleteById(id: ID)
    abstract override fun toDTO(entity: Entity): DTO
    abstract override fun toEntity(dto: CreateDTO): Entity
}

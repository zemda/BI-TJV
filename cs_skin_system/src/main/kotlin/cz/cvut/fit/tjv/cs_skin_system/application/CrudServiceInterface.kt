package cz.cvut.fit.tjv.cs_skin_system.application

interface CrudServiceInterface<Entity, ID, DTO, CreateDTO> {

    fun getById(id: ID): DTO
    fun getAll(): List<DTO>
    fun create(dto: CreateDTO): DTO
    fun deleteById(id: ID)
    fun toDTO(entity: Entity): DTO
    fun toEntity(dto: CreateDTO): Entity
}
package cz.cvut.fit.tjv.cs_skin_system.application

interface CrudServiceInterface<T, ID> {

    fun getById(id: ID): T
    fun getAll(): List<T>
    fun create(entity: T, opt: ID? = null): T
    fun deleteById(id: ID)
}
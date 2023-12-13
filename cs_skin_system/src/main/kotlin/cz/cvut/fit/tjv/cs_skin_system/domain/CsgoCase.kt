package cz.cvut.fit.tjv.cs_skin_system.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
@Entity
@Table(name = "csgo_case")
class CsgoCase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_case")
    var id: Long = 0

    @Column(name = "name_case", unique = true)
    var name: String = ""

    @DecimalMin("0.0")
    var price: Double = 0.0

    @ManyToMany(targetEntity = Skin::class, mappedBy = "dropsFrom")
    var contains: MutableSet<Skin> = mutableSetOf()
}
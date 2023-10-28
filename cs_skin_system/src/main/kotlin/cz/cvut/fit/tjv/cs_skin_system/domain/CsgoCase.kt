package cz.cvut.fit.tjv.cs_skin_system.domain

import jakarta.persistence.*


@Entity
@Table(name = "csgo_case")
class CsgoCase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_case")
    var id: Long = 0

    @Column(name = "name_case")
    var name: String = ""
    var price: Double = 0.0

    @ManyToMany(targetEntity = Skin::class, mappedBy = "dropsFrom")
    var contains: List<Skin> = listOf()
}
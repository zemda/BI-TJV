package cz.cvut.fit.tjv.cs_skin_system.domain

import jakarta.persistence.*


@Entity
@Table(name = "skin")
class Skin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_skin")
    var id: Long = 0

    @Column(name = "name_skin")
    var name: String = ""
    var rarity: String = ""
    var quality: String = ""
    var price: Double = 0.0

    @Column(name = "paint_seed")
    var paintSeed: Int = 0
    var float: Double = 0.0

    @OneToOne(targetEntity = Weapon::class, mappedBy = "skin")
    var weapon: Weapon? = null

    @ManyToMany(targetEntity = CsgoCase::class)
    @JoinTable(
        name = "skin_csog_case",
        joinColumns = [JoinColumn(name = "id_skin")],
        inverseJoinColumns = [JoinColumn(name = "id_case")]
    )
    var dropsFrom: List<CsgoCase> = listOf()

}
package cz.cvut.fit.tjv.cs_skin_system.domain

import jakarta.persistence.*


@Entity
@Table(name = "weapon")
class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_weapon")
    var id: Long = 0

    @Column(name = "name_weapon")
    var name: String = ""

    @Column(name = "type_weapon")
    var type: String = ""
    var tag: String = ""

    @OneToOne(targetEntity = Skin::class, orphanRemoval = true)
    @JoinColumn(name = "id_skin")
    var skin: Skin? = null
}
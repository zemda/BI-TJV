package cz.cvut.fit.tjv.cs_skin_system.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "weapon")
class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_weapon")
    var id: Long = 0

    @Column(name = "name_weapon") var name: String = ""

    @Column(name = "type_weapon") var type: String = ""
    var tag: String = ""

    @OneToOne(optional = false, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "id_skin")
    @JsonManagedReference
    var skin: Skin? = null
}

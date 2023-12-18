package cz.cvut.fit.tjv.cs_skin_system.domain

import com.fasterxml.jackson.annotation.*
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
@Entity
@Table(name = "skin")
class Skin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_skin")
    var id: Long = 0

    @Column(name = "name_skin") @NotBlank var name: String = ""

    @Column(name = "rarity") @NotBlank var rarity: String = ""

    @Column(name = "exterior") @NotBlank var exterior: String = ""

    @Column(name = "price") @NotNull @DecimalMin("0.0") var price: Double = 0.0

    @Column(name = "paint_seed") @NotNull @Min(0) var paintSeed: Int = 0

    @Column(name = "float") @NotNull @DecimalMin("0.0") var float: Double = 0.0

    @OneToOne(mappedBy = "skin", cascade = [CascadeType.PERSIST])
    @JsonBackReference
    var weapon: Weapon? = null

    @ManyToMany(targetEntity = CsgoCase::class)
    @JoinTable(
            name = "skin_csgo_case",
            joinColumns = [JoinColumn(name = "id_skin")],
            inverseJoinColumns = [JoinColumn(name = "id_case")]
    )
    var dropsFrom: MutableSet<CsgoCase> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Skin
        return id == other.id
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

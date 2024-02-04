package hibernateCodeFirstLab.relations.plateNumber;

import javax.persistence.*;

@Entity
@Table(name = "plate_numbers")
public class PlateNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String number;

    @OneToOne(targetEntity = CarRelation.class, mappedBy = "plateNumber")
    private CarRelation carRelation;

    public PlateNumber() {
    }

    public PlateNumber(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

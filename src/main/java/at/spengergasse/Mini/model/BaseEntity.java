package at.spengergasse.Mini.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@ToString
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // technische DB-ID

    @Column(name = "business_id", unique = true, nullable = false, updatable = false)
    private UUID businessId; // öffentliche, sichere ID

    protected BaseEntity() {
        this.businessId = UUID.randomUUID();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(businessId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return Objects.equals(businessId, that.businessId);
    }

    // -------------------------
    // TEST-HILFSMETHODE
    // -------------------------
    public void setId(Long id) {  // package-private, nur für Tests
        try {
            java.lang.reflect.Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(this, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
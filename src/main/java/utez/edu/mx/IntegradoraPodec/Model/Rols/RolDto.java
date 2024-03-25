package utez.edu.mx.IntegradoraPodec.Model.Rols;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data

public final class RolDto {
    private final String type;
    private final Long id ;

    public RolDto(String type) {
        this.type = type;
        id = null;
    }

    public RolDto(String type, Long id) {
        this.type = type;
        this.id = id;
    }
}

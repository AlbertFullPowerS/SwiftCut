package utez.edu.mx.IntegradoraPodec.Model.Category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CategoryDto {
    private final Long id ;
    private final String description;
    private final String name;
    private final String urlPhoto;

    public CategoryDto(Long id, String description, String name, String url_photo) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.urlPhoto = url_photo;
    }
}

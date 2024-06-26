package utez.edu.mx.IntegradoraPodec.Controller.Product;

import lombok.AllArgsConstructor;
import org.checkerframework.common.util.report.qual.ReportReadWrite;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.IntegradoraPodec.Config.ApiResponse;
import utez.edu.mx.IntegradoraPodec.Controller.Product.dto.ProductDto;
import utez.edu.mx.IntegradoraPodec.Services.Product.ProductService;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins={"*"})
@AllArgsConstructor

public class ProductController {
    private final ProductService service;

    //Crear
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> save(@ModelAttribute ProductDto dto){
        return  service.save(dto.toEntity() ,dto.toFile(), dto.getIdCategory());
    }
    @PostMapping("/readCategoryClient")
    public ResponseEntity<ApiResponse> getByCategoryClient(@RequestBody ProductDto dto) {
        return service.findProductsForCategory(dto.getIdCategory());
    }
    //Leer
    @PostMapping("/read")
    public ResponseEntity<ApiResponse> getById(@RequestBody ProductDto dto) {
        return service.findById(dto.getId());
    }

    @PostMapping("/readCategory")
    public ResponseEntity<ApiResponse> getByCategory(@RequestBody ProductDto dto) {
        return service.findByProductForCategory(dto.getId());
    }
    @PostMapping("/readExtras")
    public ResponseEntity<ApiResponse> getByExtras(@RequestBody ProductDto dto) {
        return service.findByProductForExtrasAll(dto.getIdExtras());
    }
    @PutMapping ("/updateQuantity")
    public ResponseEntity<ApiResponse> updateQuantity(@RequestBody ProductDto dto) {
        return service.updateQuantity(dto.toEntityId());
    }

    //Actualizar
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> update
    (@ModelAttribute ProductDto dto){
        return service.update(dto.toEntityIdSimple(), dto.toFile());
    }

    //Leer general
    @GetMapping("/readAll")
    public ResponseEntity<ApiResponse> getAll()
    {return service.getAll();}

    //eliminar
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteById(@RequestBody ProductDto dto){
        return service.deleteById(dto.getId());
    }

        @GetMapping("/readProducts")
    public ResponseEntity<ApiResponse> getAllProduct() {
        return service.findAll();
    }
}

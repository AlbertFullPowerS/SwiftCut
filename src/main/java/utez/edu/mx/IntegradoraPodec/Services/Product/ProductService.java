package utez.edu.mx.IntegradoraPodec.Services.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.mx.IntegradoraPodec.Config.ApiResponse;
import utez.edu.mx.IntegradoraPodec.Firebase.FirebaseInitializer;
import utez.edu.mx.IntegradoraPodec.Model.Cards_items.CarsItemsBean;
import utez.edu.mx.IntegradoraPodec.Model.Person.PersonBean;
import utez.edu.mx.IntegradoraPodec.Model.Product.ProductBean;
import utez.edu.mx.IntegradoraPodec.Model.Product.ProductRepository;
import utez.edu.mx.IntegradoraPodec.Model.Rols.RolsBean;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Data
public class ProductService {
    private final ProductRepository repository;
    private FirebaseInitializer firebaseInitializer;

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findById(Long id){
        Optional<ProductBean> object = repository.findById(id);
        if (object.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(object.get(), HttpStatus.OK, "Producto  encontrado"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, false, "Producto no encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    // ELIMINAR
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> deleteById(Long id) {
        Optional<ProductBean> opt = repository.findById(id);
        if (opt.isPresent()) {
            ProductBean product = opt.get();
            // Elimina la imagen asociada si existe
            if (product.getUrlPhoto() != null) {
                firebaseInitializer.delete(product.getUrlPhoto());
            }
            repository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, false, "Producto eliminado"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, true, "Producto no encontrado"), HttpStatus.NOT_FOUND);
    }


    //SELECT * FROM
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAll(){
        return new ResponseEntity<>(new ApiResponse(repository.findAll(), HttpStatus.OK,"Productos encontrados"),
                HttpStatus.OK);
    }

    // SELECT * FROM WHERE ID
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getId(ProductBean object){
        Optional<ProductBean> foundObject = repository.findById(object.getId());
        if(foundObject.isPresent())
            return new ResponseEntity<>(new ApiResponse(repository.findById(object.getId()),HttpStatus.OK,
                    "Producto encontrado"),
                    HttpStatus.OK);
        return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                "Producto invalido"),HttpStatus.BAD_REQUEST);
    }

    // CREATE
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse>save(ProductBean object , MultipartFile file ){

        ProductBean optional  = repository.saveAndFlush(object) ;
        if (optional.getName() != null){
            object.setUrlPhoto(firebaseInitializer.upload(file));
            return new ResponseEntity<>(new ApiResponse(optional
                    ,HttpStatus.OK,"Producto registrado"),HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                "Producto no registrado"),HttpStatus.BAD_REQUEST);}

    // UPDATE
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> update(ProductBean object, MultipartFile file){
        Optional<ProductBean> foundObjectOp = repository.findById(object.getId());
        if (foundObjectOp.isPresent()){

            if (file != null && !file.isEmpty()){
                if (object.getUrlPhoto() != null){
                    firebaseInitializer.delete(object.getUrlPhoto());
                }
                String imgUrl = firebaseInitializer.upload(file);
                object.setUrlPhoto(imgUrl);
            }
            return new ResponseEntity<>(new ApiResponse(repository.saveAndFlush(object),HttpStatus.OK,"Producto actualizado"),
                    HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                    "Producto invalido"), HttpStatus.BAD_REQUEST);
        }
    }
}

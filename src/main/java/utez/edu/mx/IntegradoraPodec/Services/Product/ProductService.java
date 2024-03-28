package utez.edu.mx.IntegradoraPodec.Services.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.mx.IntegradoraPodec.Config.ApiResponse;
import utez.edu.mx.IntegradoraPodec.Controller.DtoShare.CategoryProductsDto;
import utez.edu.mx.IntegradoraPodec.Firebase.FirebaseInitializer;
import utez.edu.mx.IntegradoraPodec.Model.Cards_items.CarsItemsBean;
import utez.edu.mx.IntegradoraPodec.Model.Category.CategoryBean;
import utez.edu.mx.IntegradoraPodec.Model.Category.CategoryRepository;
import utez.edu.mx.IntegradoraPodec.Model.Extras.ExtrasBean;
import utez.edu.mx.IntegradoraPodec.Model.Person.PersonBean;
import utez.edu.mx.IntegradoraPodec.Model.Product.ProductBean;
import utez.edu.mx.IntegradoraPodec.Model.Product.ProductRepository;
import utez.edu.mx.IntegradoraPodec.Model.ProductExtras.ProductExtrasBean;
import utez.edu.mx.IntegradoraPodec.Model.ProductExtras.ProductExtrasRepository;
import utez.edu.mx.IntegradoraPodec.Model.Rols.RolsBean;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Data
public class ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductExtrasRepository productExtrasRepository;
    private FirebaseInitializer firebaseInitializer;

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findById(Long id){
        System.out.println("Id prodcuto : " + id);
        Optional<ProductBean> object = repository.findByIdFast(id);
        if (object.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(object.get(), HttpStatus.OK, "Producto  encontrado"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, false, "Producto no encontrado"), HttpStatus.NOT_FOUND);
        }
    }
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findByProductForCategory(Long id){
        List<CategoryProductsDto> object = repository.findByProductForCategory(id);

            return new ResponseEntity<>(new ApiResponse(object, HttpStatus.OK, "Productos  encontrado"), HttpStatus.OK);


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
        Optional<ProductBean> foundObject = repository.findByIdFast(object.getId());
        if(foundObject.isPresent())
            return new ResponseEntity<>(new ApiResponse(foundObject.get(),HttpStatus.OK,
                    "Producto encontrado"),
                    HttpStatus.OK);
        return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                "Producto invalido"),HttpStatus.BAD_REQUEST);
    }

    // CREATE
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse>save(ProductBean object , MultipartFile file,Long id ){
            CategoryBean categoryBean = new CategoryBean();
            categoryBean.setId(id);

        ExtrasBean extrasBean = new ExtrasBean();
        extrasBean.setId(2L);

        ProductExtrasBean productExtrasBean = new ProductExtrasBean();
        productExtrasBean.setExtrasBean(extrasBean);
        productExtrasBean.setProductBean(object);

        object.setCategoryBean(categoryBean);
            object.setQuantity(0L);
        ProductBean optional  = repository.saveAndFlush(object) ;
        if (optional.getName() != null){
            object.setUrlPhoto(firebaseInitializer.upload(file));
            productExtrasRepository.save(productExtrasBean);
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
            foundObjectOp.get().setName(object.getName());
            foundObjectOp.get().setDescription(object.getDescription());


            if (file != null)
                foundObjectOp.get().setUrlPhoto(firebaseInitializer.upload(file));
            repository.saveAndFlush( foundObjectOp.get());

            return new ResponseEntity<>(new ApiResponse(object,HttpStatus.OK,"Producto actualizado"),
                    HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                    "Producto invalido"), HttpStatus.BAD_REQUEST);
        }
    }
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> updateQuantity(ProductBean object){
        Optional<ProductBean> foundObjectOp = repository.findById(object.getId());
        if (foundObjectOp.isPresent()){

            foundObjectOp.get().setQuantity(foundObjectOp.get().getQuantity() + object.getQuantity());
            repository.saveAndFlush( foundObjectOp.get());

            return new ResponseEntity<>(new ApiResponse(object,HttpStatus.OK,"Producto actualizado"),
                    HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true,
                    "Producto invalido"), HttpStatus.BAD_REQUEST);
        }
    }
}

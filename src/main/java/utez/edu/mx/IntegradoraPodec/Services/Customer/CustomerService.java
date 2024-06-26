package utez.edu.mx.IntegradoraPodec.Services.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.mx.IntegradoraPodec.Config.ApiResponse;
import utez.edu.mx.IntegradoraPodec.Firebase.FirebaseInitializer;
import utez.edu.mx.IntegradoraPodec.Model.Cart_Shop.CarShopBean;
import utez.edu.mx.IntegradoraPodec.Model.Cart_Shop.CartShopRepository;
import utez.edu.mx.IntegradoraPodec.Model.Customers.CustomerDto;
import utez.edu.mx.IntegradoraPodec.Model.Customers.CustomersBean;
import utez.edu.mx.IntegradoraPodec.Model.Customers.CustomersRepository;
import utez.edu.mx.IntegradoraPodec.Model.Employees.EmployeesBean;
import utez.edu.mx.IntegradoraPodec.Model.Employees.EmployeesDto;
import utez.edu.mx.IntegradoraPodec.Model.Person.PersonBean;
import utez.edu.mx.IntegradoraPodec.Model.Product.ProductBean;
import utez.edu.mx.IntegradoraPodec.Model.Rols.RolsBean;
import utez.edu.mx.IntegradoraPodec.Model.StatusPerson.StatusPersonBean;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Data
public class CustomerService {
    private final CustomersRepository repository;
    private FirebaseInitializer firebaseInitializer;
    private final CartShopRepository cartShopRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getEmployeeEmail(String email){
        Optional<CustomerDto> employee = repository.findByInfo(email);

        return new ResponseEntity<>(new ApiResponse(employee, HttpStatus.OK, "Empleados encontrados"), HttpStatus.OK);

    }
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findById(Long id){
        Optional<CustomersBean> object = repository.findByIdFast(id);
        if (object.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(object.get(), HttpStatus.OK, "Cliente encontrado"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, false, "No se encontro el cliente"), HttpStatus.NOT_FOUND);
        }
    }
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> findByEmail(String email , String token){
        Optional<CustomerDto> object = repository.findByEmailLocal(email);
        if (object.isPresent()) {
            object.get().setToken(token);
            return new ResponseEntity<>(new ApiResponse(object.get(), HttpStatus.OK, "Cliente encontrado"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, false, "No se encontro el cliente"), HttpStatus.NOT_FOUND);
        }
    }
    //eliminar
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse> deleteById(Long id) {
        Optional<CustomersBean> opt = repository.findById(id);
        if (opt.isPresent()){
            repository.deleteById(id);
            return  new ResponseEntity<>(new ApiResponse(HttpStatus.OK, false, "Cliente eliminado"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, true, "Cliente no encontrado"), HttpStatus.NOT_FOUND);
    }

    //SELECT * FROM
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAll(){
        return new ResponseEntity<>(new ApiResponse(repository.findAll(), HttpStatus.OK,""), HttpStatus.OK);
    }

    // SELECT * FROM WHERE ID
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getId(CustomersBean object){
        Optional<CustomersBean> foundObject = repository.findById(object.getId());
        if(foundObject.isPresent())
            return new ResponseEntity<>(new ApiResponse(repository.findById(object.getId()),HttpStatus.OK, "Registro Encontrado"), HttpStatus.OK);
        return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true, "Registro No encontrado"),HttpStatus.BAD_REQUEST);
    }

    // CREATE
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse>save(CustomersBean object, MultipartFile file){
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String encryptedPsw = bcrypt.encode(object.getPassword());
        object.setPassword(encryptedPsw);
        StatusPersonBean statusPersonBean = new StatusPersonBean();
        statusPersonBean.setId(2L);
        object.getPersonBean().setStatusPersonBean(statusPersonBean);
        object.setBlocked(true);
        object.setStatus(true);


        CustomersBean optional = repository.saveAndFlush(object);
        if (optional.getEmail() != null){
            object.getPersonBean().setUrlPhoto(firebaseInitializer.upload(file));
            CarShopBean carShopBean = new CarShopBean();
            carShopBean.setCustomersBean(object);
            cartShopRepository.save(carShopBean);
            return new ResponseEntity<>(new ApiResponse(""
                    ,HttpStatus.OK,"Cliente  registrado"),HttpStatus.OK);
        }



        return new ResponseEntity<>(new ApiResponse(
                repository.saveAndFlush(object),HttpStatus.OK,"Cliente creado"),HttpStatus.OK);
    }

    // UPDATE

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<ApiResponse>update(CustomersBean object, MultipartFile file){
        Optional<CustomersBean>foundObject = repository.findById(object.getId());
        if (!foundObject.isPresent())
            return new ResponseEntity<>(new ApiResponse((HttpStatus.BAD_REQUEST), true, "Empleado no encontrado"),HttpStatus.BAD_REQUEST);



        if (object.getPersonBean().getName() !=null) foundObject.get().getPersonBean().setName(object.getPersonBean().getName());
        if (object.getPersonBean().getPhone() !=null) foundObject.get().getPersonBean().setPhone(object.getPersonBean().getPhone());
        if (object.getPersonBean().getSex() !=null) foundObject.get().getPersonBean().setSex(object.getPersonBean().getSex());
        if (object.getEmail() !=null) foundObject.get().setEmail(object.getEmail());
        if (object.getPersonBean().getLastName() !=null) foundObject.get().getPersonBean().setLastName(object.getPersonBean().getLastName());




        if (object.getPassword() !=null){
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            String encryptedPsw = bcrypt.encode(object.getPassword());
            foundObject.get().setPassword(encryptedPsw);
        }


        if (file != null)
            foundObject.get().getPersonBean().setUrlPhoto(firebaseInitializer.upload(file));


        repository.saveAndFlush(foundObject.get());
        // repository.saveAndFlush(object)
        return new ResponseEntity<>(new ApiResponse(""
                ,HttpStatus.OK,"Cliente  Actualizado"),HttpStatus.OK);
    }


}

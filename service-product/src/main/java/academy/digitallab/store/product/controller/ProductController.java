package academy.digitallab.store.product.controller;

import academy.digitallab.store.product.entity.Category;
import academy.digitallab.store.product.entity.Product;
import academy.digitallab.store.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> listProduct(){
        List<Product> products = productService.listAllProduct();
        if(products.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/category")
    public ResponseEntity<List<Product>> listProductByCategory(@RequestParam(name = "categoryId", required = false) Long categoryId){
        List<Product> products;
        if(categoryId == null){
            products = productService.listAllProduct();
            if(products.isEmpty()){
                return ResponseEntity.noContent().build();
            }
        }else{
            products = productService.findByCategory(Category.builder().id(categoryId).build());
            if(products.isEmpty()){
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProduct (@PathVariable("id") Long id){
        Product product = productService.getProduct(id);
        if(product == null){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok(product);
        }
    }

    @PostMapping
    public ResponseEntity <Product> createProduct(@Valid @RequestBody Product product, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , this.formatMessage(bindingResult));
        }
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity <Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product){
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        if(updatedProduct == null){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok(updatedProduct);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity <Product> deleteProduct(@PathVariable("id") Long id){
        Product deletedProduct = productService.deleteProduct(id);
        if(deletedProduct == null){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(deletedProduct);
        }
    }

    @PatchMapping(value = "/{id}/stock")
    public ResponseEntity<Product> updateProductStock( @PathVariable("id") Long id, @RequestParam("quantity") Double quantity){
        Product product = productService.updateStock(id, quantity);
        if(product == null){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(product);
        }
    }

    /**
     * Formats the error messages from the given BindingResult object into a JSON string.
     * @param result the BindingResult object containing the error messages
     * @return a JSON string containing the formatted error messages
     */
    private String formatMessage(BindingResult result) {

        // Map each field error to a map containing the field name and error message
        List<Map<String, String>> errors = result.getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());

        // Build the ErrorMessage object
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();

        // Convert the ErrorMessage object to JSON string
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;

    }

}

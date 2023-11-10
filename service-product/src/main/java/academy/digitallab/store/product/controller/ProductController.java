package academy.digitallab.store.product.controller;

import academy.digitallab.store.product.entity.Category;
import academy.digitallab.store.product.entity.Product;
import academy.digitallab.store.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity <Product> createProduct(@RequestBody Product product){
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
}
